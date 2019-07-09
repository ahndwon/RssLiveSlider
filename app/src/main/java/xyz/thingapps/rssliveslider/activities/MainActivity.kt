package xyz.thingapps.rssliveslider.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()
    }
}
