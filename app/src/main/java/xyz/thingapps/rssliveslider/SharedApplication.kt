package xyz.thingapps.rssliveslider

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.Instant
import xyz.thingapps.rssliveslider.models.RssUrl
import xyz.thingapps.rssliveslider.utils.fromJson

class SharedApplication : Application() {

    private val gson = Gson()

    var rssUrlList: ArrayList<RssUrl>?
        get() {
            val json = preferences.getString(PREFERENCE_RSS_URLS, null) ?: return null
            return gson.fromJson<ArrayList<RssUrl>>(json)
        }
        set(value) {
            if (value != null) {
                val json = gson.toJson(value)
                preferences.edit().putString(PREFERENCE_RSS_URLS, json).apply()
            }
        }

    private val preferences: SharedPreferences
        get() = getSharedPreferences(PREFERENCE_DOCUMENT_NAME, Context.MODE_PRIVATE)

    fun addRssUrl(rssUrl: RssUrl) {
        val list = rssUrlList
        list?.add(rssUrl)
        rssUrlList = list
    }


    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (rssUrlList == null) {
            populateRssList()
        }
    }

    private fun populateRssList() {
        rssUrlList = arrayListOf(
            RssUrl("https://rss.joins.com/joins_news_list.xml", Instant.now().toEpochMilli()),
            RssUrl("http://www.chosun.com/site/data/rss/rss.xml", Instant.now().toEpochMilli()),
            RssUrl("https://www.nasa.gov/rss/dyn/breaking_news.rss", Instant.now().toEpochMilli()),
            RssUrl(
                "https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss",
                Instant.now().toEpochMilli()
            ),
            RssUrl(
                "https://www.nasa.gov/rss/dyn/onthestation_rss.rss",
                Instant.now().toEpochMilli()
            ),
            RssUrl(
                "https://www.nasa.gov/rss/dyn/mission_pages/kepler/news/kepler-newsandfeatures-RSS.rss",
                Instant.now().toEpochMilli()
            ),
            RssUrl("https://www.nasa.gov/rss/dyn/chandra_images.rss", Instant.now().toEpochMilli()),
            RssUrl("https://www.nasa.gov/rss/dyn/TWAN_vodcast.rss", Instant.now().toEpochMilli()),
            RssUrl(
                "https://www.nasa.gov/rss/dyn/shuttle_station.rss",
                Instant.now().toEpochMilli()
            ),
            RssUrl("https://www.nasa.gov/rss/dyn/solar_system.rss", Instant.now().toEpochMilli())
        )
    }

    companion object {
        const val PREFERENCE_RSS_URLS = "rss_urls"
        const val PREFERENCE_DOCUMENT_NAME = "xyz.thingapps.rssliveslider"
    }
}

val Context.sharedApp: SharedApplication
    get() = applicationContext as SharedApplication