package xyz.thingapps.rssliveslider.api

import io.reactivex.Observable
import org.jsoup.nodes.Document
import retrofit2.http.GET
import retrofit2.http.Url

interface JSoupApi {
    @GET
    fun getDocument(@Url url: String): Observable<Document>
}