package xyz.thingapps.rssliveslider.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.api.provideJtbcApi
import xyz.thingapps.rssliveslider.api.provideNasaApi

class HomeViewModel : ViewModel() {
    private val disposeBag = CompositeDisposable()

    var fragmentList = ArrayList<Fragment>()

    var castList: Observable<ArrayList<Observable<Cast>>> = Observable.just(ArrayList())
//    var castList: Observable<ArrayList<Cast>> = Observable.just(ArrayList())

//    var castList: ArrayList<Observable<Cast>> = ArrayList()
//    var obsList: ArrayList<Observable<Cast>> = ArrayList()
//    var casts : Observable<Unit>? = null

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
//        getVodCast()
//        getBreakingNews()
//        getImageOfDay()
//        getOnTheStation()
//        getKepler()
//        getChandra()
//        getShuttleStation()
//        getSolarSystem()

        castList = Observables.zip(
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
                Observable.just(vod),
                Observable.just(news),
                Observable.just(imageOfDay),
                Observable.just(onStation),
                Observable.just(kepler),
                Observable.just(chandra),
                Observable.just(shuttleStation),
                Observable.just(solar)
            )
        }
//            .repeatWhen { o -> o.concatMap { v -> Observable.timer(1000, TimeUnit.MILLISECONDS) } }
//            .distinctUntilChanged()
        

    }

    private fun addToList(cast: Observable<Cast>) {
//        obsList.add(cast)

//        castList.subscribe { arrayList ->
//            cast.subscribe {
//                arrayList.add(it)
//            }
//        }.addTo(disposeBag)

//        castList.add(cast)

//        castList.subscribe({
//            it.add(cast)
//        },
//            { e ->
//                e.printStackTrace()
//            })
//            .addTo(disposeBag)
    }

    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }
}