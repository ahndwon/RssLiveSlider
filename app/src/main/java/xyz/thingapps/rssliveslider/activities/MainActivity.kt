package xyz.thingapps.rssliveslider.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.api.provideNasaApi
import xyz.thingapps.rssliveslider.fragments.HomeFragment
import xyz.thingapps.rssliveslider.utils.enqueue
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.queryTextChanges()
            ?.debounce(500, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {

                Log.d("MainActivity", "SEARCH===$it")
            }
            ?.addTo(disposeBag)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.search -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}

