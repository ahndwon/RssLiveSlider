package xyz.thingapps.rssliveslider.api

import io.reactivex.Observable
import retrofit2.http.GET
import xyz.thingapps.rssliveslider.api.dao.Cast


interface NasaApi {
    @GET("TWAN_vodcast.rss")
    fun getVodCast(): Observable<Cast>

    @GET("breaking_news.rss")
    fun getBreakingNews(): Observable<Cast>

    @GET("lg_image_of_the_day.rss")
    fun getImageOfDay(): Observable<Cast>

    @GET("onthestation_rss.rss")
    fun getOnTheStation(): Observable<Cast>

    @GET("mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss")
    fun getKepler(): Observable<Cast>

    @GET("chandra_images.rss")
    fun getChandra(): Observable<Cast>

    @GET("shuttle_station.rss")
    fun getShuttleStation(): Observable<Cast>

    @GET("solar_system.rss")
    fun getSolarSystem(): Observable<Cast>
}

interface JtbcApi {
    @GET("newsflash.xml")
    fun getNewsFlashCast(): Observable<Cast>
}