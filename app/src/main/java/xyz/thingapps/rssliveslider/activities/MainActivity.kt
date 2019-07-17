package xyz.thingapps.rssliveslider.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.fragments.HomeFragment
import xyz.thingapps.rssliveslider.viewmodels.HomeViewModel
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()
    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as? SearchView

        val sourceObservable = Observable.range(1, 10)

//        val filteredObservable = sourceObservable.filter {
////
////        }

        searchView?.queryTextChanges()
            ?.debounce(500, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { search ->
                Log.d("MainActivity", "search : $search")
//                viewModel.castList
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .flatMap {
//                        it.flatMap {
//
//                        }
//                    }

//                    .subscribe { castList ->
//                        castList.flatMap { it ->
//                            it
//                        }
//
//                        castList.forEach { observableCast ->
//                            observableCast.filter { cast ->
//                                cast.title.contains(search)
//                            }.subscribe {
//
//                            }
//                        }
//                    }
            }
            ?.addTo(disposeBag)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}

