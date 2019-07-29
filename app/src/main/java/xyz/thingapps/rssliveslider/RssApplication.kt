package xyz.thingapps.rssliveslider

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class RssApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}