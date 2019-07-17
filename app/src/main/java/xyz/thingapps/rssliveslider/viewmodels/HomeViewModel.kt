package xyz.thingapps.rssliveslider.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.api.provideJtbcApi
import xyz.thingapps.rssliveslider.api.provideNasaApi

class HomeViewModel : ViewModel() {
    var castList: Observable<ArrayList<Observable<Cast>>> = Observable.just(ArrayList())

    fun getVodCast(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getVodCast()
        addToList(cast, disposeBag)
    }

    fun getBreakingNews(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getBreakingNews()
        addToList(cast, disposeBag)
    }

    fun getImageOfDay(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getImageOfDay()
        addToList(cast, disposeBag)
    }

    fun getOnTheStation(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getOnTheStation()
        addToList(cast, disposeBag)
    }

    fun getKepler(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getKepler()
        addToList(cast, disposeBag)
    }

    fun getChandra(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getChandra()
        addToList(cast, disposeBag)
    }

    fun getShuttleStation(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getShuttleStation()
        addToList(cast, disposeBag)
    }

    fun getSolarSystem(disposeBag: CompositeDisposable) {
        val cast = provideNasaApi().getSolarSystem()
        addToList(cast, disposeBag)
    }

    fun getNewsFlash(disposeBag: CompositeDisposable) {
        val cast = provideJtbcApi().getNewsFlashCast()
        addToList(cast, disposeBag)
//        castList = Observable.just(listOf(cast))
    }

    private fun addToList(cast: Observable<Cast>, disposeBag: CompositeDisposable) {
        castList.subscribe({
            it.add(cast)
        },
            { e ->
                e.printStackTrace()
            })
            .addTo(disposeBag)
    }
}