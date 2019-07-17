package xyz.thingapps.rssliveslider.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import xyz.thingapps.rssliveslider.api.dao.Cast

class CastViewModel(private val cast: Observable<Cast>) : ViewModel() {

}