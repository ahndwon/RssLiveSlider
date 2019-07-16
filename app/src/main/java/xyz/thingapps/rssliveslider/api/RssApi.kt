package xyz.thingapps.rssliveslider.api

import retrofit2.Call
import retrofit2.http.GET
import xyz.thingapps.rssliveslider.api.dao.Cast

interface NasaApi {
    @GET("dyn/TWAN_vodcast.rss")
    fun getVodCast(): Call<Cast>
}