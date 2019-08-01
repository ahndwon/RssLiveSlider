package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment
import xyz.thingapps.rssliveslider.fragments.HomeFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()
    private val filterDialog = FilterDialogFragment()
    private val viewModel: RssViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(RssViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_search)

        viewModel.imageRecognizer.setClassifier(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()

        filterBar.clicks().throttleFirst(FILTER_DURATION, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (viewModel.castList.isNullOrEmpty()) {
                    return@subscribe
                }

                if (filterDialog.isAdded) {
                    return@subscribe
                }

                filterDialog.show(supportFragmentManager, FilterDialogFragment.TAG)
            }, { e ->
                Log.d(MainActivity::class.java.name, "e : ", e)
            }).addTo(disposeBag)


        buttonClearFilter.setOnClickListener {
            viewModel.getData()
            viewModel.sort = FilterDialogFragment.ADD_ASCENDING
        }

        viewModel.castListPublisher
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ castList ->
                val currentRssText =
                    if (viewModel.castTitleList.size == castList.size) getString(R.string.all_rss)
                    else castList.map { it.title }.reversed().joinToString(separator = ", ")

                textCurrentSearch.text = currentRssText
                textCurrentSortBy.text = viewModel.sort
            }, { e ->
                Log.d(MainActivity::class.java.name, "e : ", e)
            })
            .addTo(disposeBag)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rss_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val list = ArrayList<Cast>()
                list.addAll(viewModel.castList)
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.CAST_LIST, list)
                startActivity(intent)
            }

            R.id.rss_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, RC_RSS_URL)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_RSS_URL -> viewModel.getData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val RC_RSS_URL = 333
        const val FILTER_DURATION = 600L
    }
}

