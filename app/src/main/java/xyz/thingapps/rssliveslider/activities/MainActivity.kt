package xyz.thingapps.rssliveslider.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.api.provideNasaApi
import xyz.thingapps.rssliveslider.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()

        val rssApi = provideNasaApi()
        val rssCall = rssApi.getVodCast()
        rssCall.enqueue({
            val statusCode = it.code()
            if(statusCode == 200) {
                Log.i("rss", it.body().toString())
            }
        }, {
            Log.i("rss","error : ", it)
        })
    }
}


fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = failure(t)
        override fun onResponse(call: Call<T>, response: Response<T>) = success(response)
    })
}
