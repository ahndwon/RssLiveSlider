package xyz.thingapps.rssliveslider.api

import io.reactivex.Observable
import retrofit2.http.GET
import xyz.thingapps.rssliveslider.api.dao.Cast

interface NasaApi {
    @GET("dyn/TWAN_vodcast.rss")
    fun getVodCast(): Observable<Cast>
}