package xyz.thingapps.rssliveslider.viewmodels

import android.app.Application
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import xyz.thingapps.rssliveslider.api.provideRssApi
import xyz.thingapps.rssliveslider.fragments.ChannelFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.sharedApp
import xyz.thingapps.rssliveslider.tflite.ImageRecognizer

class RssViewModel(val app: Application) : AndroidViewModel(app) {
    private val disposeBag = CompositeDisposable()
    var currentFragmentPublisher = PublishSubject.create<Int>()

    var urlList: List<String> = (app.sharedApp.rssUrlList ?: ArrayList()).map {
        it.url
    }

    var sortList: List<String> = listOf("Date Ascending")

    val imageRecognizer = ImageRecognizer(getApplication())


    var castList: List<Cast> = emptyList()
        set(value) {
            setChannels(value)
            field = value
            castListPublisher.onNext(value)

            setImageRecognitions(castList)
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
                castTitleList = (castList.map { cast -> cast.title ?: "" }).toMutableList()

                onSubscribe?.invoke()
            }, { e ->
                e.printStackTrace()
            }).addTo(disposeBag)
    }

    fun filter(rss: MutableSet<String>, sort: MutableSet<String>) {

        val onSubscribe = {
            castList = castList.filter {
                rss.contains(it.title)
            }
        }
        getData {
            if (!rss.contains("All RSS"))
                onSubscribe.invoke()

            castList = castList.sort(sort)
            sortList = sort.toList()

        }
    }

    private fun List<Cast>.sort(sort: MutableSet<String>): List<Cast> {
        var list = this

        if (sort.contains("Add Ascending")) {
            list = this.sortedBy { it.createdAt }
        }
        if (sort.contains("Add Descending")) {
            list = this.sortedByDescending { it.createdAt }
        }
        if (sort.contains("Title Ascending")) {
            list = this.sortedBy { it.title }
        }
        if (sort.contains("Title Descending")) {
            list = this.sortedByDescending { it.title }
        }

        return list
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