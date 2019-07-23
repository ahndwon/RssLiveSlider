package xyz.thingapps.rssliveslider.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.api.provideNasaApi
import xyz.thingapps.rssliveslider.fragments.ChannelFragment

class HomeViewModel : ViewModel() {
    private val disposeBag = CompositeDisposable()

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
        Observables.zip(
            provideNasaApi().getVodCast(),
            provideNasaApi().getBreakingNews(),
            provideNasaApi().getImageOfDay(),
            provideNasaApi().getOnTheStation(),
            provideNasaApi().getKepler(),
            provideNasaApi().getChandra(),
            provideNasaApi().getShuttleStation(),
            provideNasaApi().getSolarSystem()
        ) { vod, news, imageOfDay, onStation, kepler, chandra, shuttleStation, solar ->
            arrayListOf(
                vod,
                news,
                imageOfDay,
                onStation,
                kepler,
                chandra,
                shuttleStation,
                solar
            )
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