package xyz.thingapps.rssliveslider.viewmodels

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import xyz.thingapps.rssliveslider.api.Cast
import xyz.thingapps.rssliveslider.api.provideRssApi
import xyz.thingapps.rssliveslider.fragments.ChannelFragment
import xyz.thingapps.rssliveslider.tflite.ImageRecognizer

class RssViewModel(application: Application) : AndroidViewModel(application) {
    private val disposeBag = CompositeDisposable()
    var currentFragmentPublisher = PublishSubject.create<Int>()

    var urlList: ArrayList<String> = arrayListOf(
        "https://rss.joins.com/joins_news_list.xml",
        "http://www.chosun.com/site/data/rss/rss.xml",
        "https://www.nasa.gov/rss/dyn/TWAN_vodcast.rss",
        "https://www.nasa.gov/rss/dyn/breaking_news.rss",
        "https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss",
        "https://www.nasa.gov/rss/dyn/onthestation_rss.rss",
        "https://www.nasa.gov/rss/dyn/mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss",
        "https://www.nasa.gov/rss/dyn/chandra_images.rss",
        "https://www.nasa.gov/rss/dyn/shuttle_station.rss",
        "https://www.nasa.gov/rss/dyn/solar_system.rss"
    )

    val recognitionList: ArrayList<String> = ArrayList()

    var castList: List<Cast> = emptyList()
        set(value) {
            setChannels(value)
            field = value
            castListPublisher.onNext(value)

            ImageRecognizer(getApplication()).getRecognitions(value[0].items[0].media!!.url)
                .subscribe({ results ->
                    val labels = results.map {
                        it.title
                    }
                    recognitionList.addAll(labels)
                }, {

                }).addTo(disposeBag)
        }

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