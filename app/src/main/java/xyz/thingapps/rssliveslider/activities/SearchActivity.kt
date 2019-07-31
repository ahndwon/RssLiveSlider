package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_search.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.SearchGroupListAdapter
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.viewmodels.SearchViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    val adapter = SearchGroupListAdapter()
    private val disposeBag = CompositeDisposable()
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupActionBar()

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        viewModel.imageRecognizer.setClassifier(this)
        viewModel.castList = intent?.getParcelableArrayListExtra(CAST_LIST) ?: ArrayList()
        viewModel.setImageRecognitions()

        setupAdapter()
        setupRecyclerView()

        setSearch(viewModel.castList)
    }

    private fun setSearch(castList: ArrayList<Cast>) {
        searchView?.queryTextChanges()
            ?.debounce(QUERY_TIMEOUT, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ search ->
                if (search.isBlank()) return@subscribe

                adapter.items = query(castList, search.toString())
                adapter.notifyDataSetChanged()
            }, { e ->
                e.printStackTrace()
            })?.addTo(disposeBag)
    }

    private fun query(castList: List<Cast>, search: String): List<Cast> {
        return castList.map { cast ->
            val filtered = cast.items?.filter { item ->
                item.title?.contains(search) ?: false || item.recognition == search
            } ?: emptyList()
            val filteredCast = Cast(cast.title, cast.description, cast.link, filtered)
            filteredCast
        }.filter {
            it.items?.isNotEmpty() ?: false
        }
    }

    private fun setupAdapter() {
        adapter.onItemClick = { item ->
            val intent = Intent(this@SearchActivity, ItemDetailActivity::class.java)
            intent.putExtra(ItemDetailActivity.RSS_ITEM, item)
            this@SearchActivity.startActivity(intent)
        }

        adapter.onGroupClick = { cast ->
            val intent = Intent(this@SearchActivity, AllContentsActivity::class.java)
            intent.putExtra(AllContentsActivity.RSS_CAST, cast)
            this@SearchActivity.startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        searchRecyclerView.adapter = adapter
        searchRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }

    companion object {
        const val CAST_LIST = "cast_list"
        const val QUERY_TIMEOUT = 500L
    }
}
