package xyz.thingapps.rssliveslider.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.api.provideJtbcApi
import xyz.thingapps.rssliveslider.api.provideNasaApi

class HomeViewModel : ViewModel() {
    private val disposeBag = CompositeDisposable()
    var castList: Observable<ArrayList<Observable<Cast>>> = Observable.just(ArrayList())

    private fun getVodCast() = addToList(provideNasaApi().getVodCast())

    private fun getBreakingNews() = addToList(provideNasaApi().getBreakingNews())

    private fun getImageOfDay() = addToList(provideNasaApi().getImageOfDay())

    private fun getOnTheStation() = addToList(provideNasaApi().getOnTheStation())

    private fun getKepler() = addToList(provideNasaApi().getKepler())

    private fun getChandra() = addToList(provideNasaApi().getChandra())

    private fun getShuttleStation() = addToList(provideNasaApi().getShuttleStation())

    private fun getSolarSystem() = addToList(provideNasaApi().getSolarSystem())

    fun getNewsFlash() = addToList(provideJtbcApi().getNewsFlashCast())

    fun getData() {
        getVodCast()
        getBreakingNews()
        getImageOfDay()
        getOnTheStation()
        getKepler()
        getChandra()
        getShuttleStation()
        getSolarSystem()
    }

    private fun addToList(cast: Observable<Cast>) {
        castList.subscribe({
            it.add(cast)
        },
            { e ->
                e.printStackTrace()
            })
            .addTo(disposeBag)
    }

    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }
}