package xyz.thingapps.rssliveslider.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.api.provideNasaApi

class HomeViewModel : ViewModel() {
    private var castList: Observable<List<Observable<Cast>>> = Observable.just(emptyList())

    fun getRss() {
        val cast = provideNasaApi().getVodCast()
        castList = Observable.just(listOf(cast))
    }
}