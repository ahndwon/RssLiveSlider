package xyz.thingapps.rssliveslider.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.api.provideRssApi
import xyz.thingapps.rssliveslider.fragments.ChannelFragment

class RssViewModel : ViewModel() {
    private val disposeBag = CompositeDisposable()
    var currentFragmentPublisher = PublishSubject.create<Int>()

    var urlList: ArrayList<String> = arrayListOf(
        "https://www.nasa.gov/rss/dyn/TWAN_vodcast.rss",
        "https://www.nasa.gov/rss/dyn/breaking_news.rss",
        "https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss",
        "https://www.nasa.gov/rss/dyn/onthestation_rss.rss",
        "https://www.nasa.gov/rss/dyn/mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss",
        "https://www.nasa.gov/rss/dyn/chandra_images.rss",
        "https://www.nasa.gov/rss/dyn/shuttle_station.rss",
        "https://www.nasa.gov/rss/dyn/solar_system.rss"
    )

    var castList: List<Cast> = emptyList()
        set(value) {
            setChannels(value)
            field = value
            castListPublisher.onNext(value)
        }

    var channelList: List<Fragment> = emptyList()

    var castListPublisher = PublishSubject.create<List<Cast>>()

    private fun setChannels(castList: List<Cast>) {
        val fragments = ArrayList<Fragment>()
        castList.forEachIndexed { index, cast ->
            fragments.add(ChannelFragment.newInstance(cast.title, index))
        }
        channelList = fragments.toList()
    }

    fun getData(onSubscribe: (() -> Unit)? = null) {
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
                castList = it
                onSubscribe?.invoke()
            }, { e ->
                e.printStackTrace()
            }).addTo(disposeBag)
    }


    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }
}