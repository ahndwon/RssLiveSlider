package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    companion object {
        const val CAST_LIST = "cast_list"
    }

    private val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupActionBar()

        val castList: ArrayList<Cast> =
            intent?.getParcelableArrayListExtra(CAST_LIST) ?: ArrayList()

        val adapter = SearchGroupListAdapter()
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

        searchRecyclerView.adapter = adapter
        searchRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        searchView?.queryTextChanges()
            ?.debounce(500, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ search ->
                if (search.isBlank()) return@subscribe
                val result = castList.map { cast ->
                    val filtered = cast.items?.filter { item ->
                        item.title.contains(search)
                    } ?: emptyList()
                    val filteredCast = Cast(cast.title, cast.description, cast.link, filtered)
                    filteredCast
                }.filter {
                    it.items?.isNotEmpty() ?: false
                }

                Log.d(SearchActivity::class.java.name, "result : $result")

                adapter.items = result
                adapter.notifyDataSetChanged()
            }, { e ->
                e.printStackTrace()
            })?.addTo(disposeBag)
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
}
