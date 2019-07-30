package xyz.thingapps.rssliveslider.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url
import xyz.thingapps.rssliveslider.models.Cast

interface RssApi {
    @GET
    fun getCast(@Url url: String): Observable<Cast>
}