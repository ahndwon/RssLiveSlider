package xyz.thingapps.rssliveslider.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_all_contents.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.ContentItemListAdapter
import xyz.thingapps.rssliveslider.models.Cast

class AllContentsActivity : AppCompatActivity() {

    companion object {
        const val RSS_CAST = "cast"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_contents)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cast: Cast? = intent?.getParcelableExtra(RSS_CAST)

        supportActionBar?.title = cast?.title

        val adapter = ContentItemListAdapter()
        cast?.let {
            adapter.items = it.items ?: emptyList()
        }

        contentRecyclerView.adapter = adapter
        contentRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
