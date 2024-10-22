package xyz.thingapps.rssliveslider.viewmodels

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import xyz.thingapps.rssliveslider.api.provideRssApi
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.ADD_ASCENDING
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.ADD_DESCENDING
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.TITLE_ASCENDING
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment.Companion.TITLE_DESCENDING
import xyz.thingapps.rssliveslider.fragments.ChannelFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.models.RssUrl
import xyz.thingapps.rssliveslider.sharedApp
import xyz.thingapps.rssliveslider.tflite.ImageRecognizer


class RssViewModel(val app: Application) : AndroidViewModel(app) {
    private var urlList: List<RssUrl> = (app.sharedApp.rssUrlList ?: ArrayList())

    var sort = ADD_ASCENDING

    val imageRecognizer = ImageRecognizer(getApplication())
    var currentFragmentPublisher = PublishSubject.create<Int>()

    private val disposeBag = CompositeDisposable()

    var castList: List<Cast> = emptyList()
        set(value) {
            setChannels(value)
            field = value
            castListPublisher.onNext(value)
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

    private fun getRssUrlList(): List<RssUrl> {
        return (app.sharedApp.rssUrlList ?: ArrayList())
    }

    fun getData(onSubscribe: (() -> Unit)? = null) {
        urlList = getRssUrlList()

        Observable.zip(
            urlList.map {
                provideRssApi().getCast(it.url)
            }
        ) {
            it.map { data ->
                (data as Cast).apply {
                    data.createdAt = urlList[it.indexOf(data)].createdAt
                }
            }
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
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

