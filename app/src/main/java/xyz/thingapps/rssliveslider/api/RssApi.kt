package xyz.thingapps.rssliveslider.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface RssApi {
    @GET
    fun getCast(@Url url: String): Observable<Cast>
}