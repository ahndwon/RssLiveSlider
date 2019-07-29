package xyz.thingapps.rssliveslider.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_item_detail.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.api.Item

class ItemDetailActivity : AppCompatActivity() {

    companion object {
        const val RSS_ITEM = "rss_item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item = intent.getParcelableExtra<Item>(RSS_ITEM)

        titleTextView.text = item.title
        itemInfoTextView.text = item.pubDate
        descriptionTextView.text = item.description

        Log.d(ItemDetailActivity::class.java.name, "media ${item.media}")

        item.media?.let {
            if (it.type.contains("image")) {
                Glide.with(itemImageView)
                    .load(it.url)
                    .into(itemImageView)

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
