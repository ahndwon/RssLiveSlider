package xyz.thingapps.rssliveslider.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import xyz.thingapps.rssliveslider.api.provideJSoupApi
import xyz.thingapps.rssliveslider.api.provideRssApi
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.ADD_ASCENDING
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.ADD_DESCENDING
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.TITLE_ASCENDING
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.TITLE_DESCENDING
import xyz.thingapps.rssliveslider.fragments.ChannelFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.models.GlideSize
import xyz.thingapps.rssliveslider.models.Item
import xyz.thingapps.rssliveslider.models.Media
import xyz.thingapps.rssliveslider.sharedApp
import xyz.thingapps.rssliveslider.tflite.ImageRecognizer
import xyz.thingapps.rssliveslider.viewholders.ChannelItemViewHolder


class RssViewModel(val app: Application) : AndroidViewModel(app) {
    private val disposeBag = CompositeDisposable()
    var currentFragmentPublisher = PublishSubject.create<Int>()

    private var urlList: List<String> = (app.sharedApp.rssUrlList ?: ArrayList()).map {
        it.url
    }

    var sort = ADD_ASCENDING

    val imageRecognizer = ImageRecognizer(getApplication())


    var castList: List<Cast> = emptyList()
        set(value) {
            setChannels(value)
            field = value
            castListPublisher.onNext(value)

//            setImageRecognitions(castList)
            parseJSoup(castList)
        }

    var castTitleList = mutableListOf<String>()

    var channelList: List<Fragment> = emptyList()
        set(value) {
            field = value
            channelListPublisher.onNext(value)
        }

    var castListPublisher = PublishSubject.create<List<Cast>>()
    var channelListPublisher = PublishSubject.create<List<Fragment>>()

    private fun setChannels(castList: List<Cast>) {
        val fragments = ArrayList<Fragment>()
        castList.forEachIndexed { index, cast ->
            fragments.add(ChannelFragment.newInstance(cast.title ?: "", index))
        }
        channelList = fragments.toList()
    }

    private fun setImageRecognitions(castList: List<Cast>) {
        castList.forEach { cast ->
            cast.items?.forEach { item ->
                if (item.media?.type?.contains("image") == true) {
                    item.media?.url?.let { url ->
                        imageRecognizer.getRecognitions(url)
                            .subscribeOn(Schedulers.io())
                            .subscribe({ results ->
                                val label = results.maxBy {
                                    it.confidence
                                }?.title ?: ""

                                Log.d("ImageRecognizer", "label : $label")

                                if (label.isNotBlank())
                                    item.recognition = label
                            }, { e ->
                                Log.e("ImageRecognizer", "recognize image failed : ", e)
                            }).addTo(disposeBag)
                    }
                }
            }
        }
    }

    private fun parseJSoup(castList: List<Cast>) {
        castList.forEach { cast ->
            cast.items?.forEach { item ->
                if (item.media == null) {
                    item.link?.let { link ->
                        provideJSoupApi().getDocument(link)
                            .subscribeOn(Schedulers.io())
                            .subscribe({ document ->
                                val urlList =
                                    document.select("img").map { element ->
                                        when {
                                            element.hasAttr("src") -> element.attr("src")
                                            element.hasAttr("data-src") -> element.attr("data-src")
                                            else -> ""
                                        }
                                    }
                                getMaxImage(urlList, item)
                            }, { e ->
                                Log.i(
                                    ChannelItemViewHolder::class.java.name,
                                    "getDocument error : ",
                                    e
                                )
                            })
                    }
                }
            }
        }
    }

    private fun getMaxImage(urlList: List<String>, item: Item) {
        Observable.zip(urlList.map {
            getGlideSize(it)
        }) { data ->
            data.map {
                it as GlideSize
            }.toList().maxBy { size ->
                size.width * size.height
            }
        }.subscribeOn(Schedulers.io())
            .subscribe({ max ->
                max?.let {
                    item.media = Media(max.url, "image")
                }
            }, { e ->
                Log.d(
                    RssViewModel::class.java.name,
                    "getGlideSize failed : ",
                    e
                )
            }).addTo(disposeBag)
    }

    private val sizeOptions by lazy {
        RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    private fun getGlideSize(url: String): Observable<GlideSize> {
        return Observable.create { emitter ->
            Glide.with(app)
                .`as`(GlideSize::class.java)
                .apply(sizeOptions)
                .load(url)
                .into(object : SimpleTarget<GlideSize>() {
                    override fun onResourceReady(
                        resource: GlideSize,
                        transition: Transition<in GlideSize>?
                    ) {
                        emitter.onNext(resource.apply {
                            this.url = url
                        })
                        emitter.onComplete()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Log.d("GlideImage", "image size error : $errorDrawable")
                    }
                })
        }
    }

    private fun getRssUrlList(): List<String> {
        return (app.sharedApp.rssUrlList ?: ArrayList()).map {
            it.url
        }
    }

    fun getData(onSubscribe: (() -> Unit)? = null) {
        urlList = getRssUrlList()

        Observable.zip(
            urlList.map {
                provideRssApi().getCast(it)
            }
        ) {
            it.map { data ->
                data as Cast
            }
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setRssTitle(it)
                castList = it
                castTitleList =
                    (castList.map { cast -> cast.title ?: "" }).reversed().toMutableList()

                onSubscribe?.invoke()
            }, { e ->
                e.printStackTrace()
            }).addTo(disposeBag)
    }

    fun filter(rss: MutableSet<String>, sort: String) {
        val onSubscribe = {
            castList = castList.filter {
                rss.contains(it.title)
            }
        }

        getData {
            onSubscribe.invoke()
            castList = castList.sort(sort)
            this.sort = sort
        }
    }

    private fun List<Cast>.sort(sort: String): List<Cast> {
        return when (sort) {
            ADD_ASCENDING -> this.sortedBy { it.createdAt }

            ADD_DESCENDING -> this.sortedByDescending { it.createdAt }

            TITLE_ASCENDING -> this.sortedBy { it.title }

            TITLE_DESCENDING -> this.sortedByDescending { it.title }

            else -> this
        }
    }

    private fun setRssTitle(castList: List<Cast>) {
        val rssUrlList = app.sharedApp.rssUrlList ?: ArrayList()
        var isChange = false
        for (i in 0.until(castList.size)) {
            val rssUrl = rssUrlList[i]
            if (rssUrl.title == null) {
                rssUrl.title = castList[i].title
                isChange = true
            }
        }

        if (isChange) app.sharedApp.rssUrlList = rssUrlList
    }

    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }
}

