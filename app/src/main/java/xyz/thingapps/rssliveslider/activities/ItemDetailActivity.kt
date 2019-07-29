package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_item_detail.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.activities.WebViewActivity.Companion.ITEM_URL
import xyz.thingapps.rssliveslider.api.Item
import java.util.concurrent.TimeUnit

class ItemDetailActivity : AppCompatActivity() {

    companion object {
        const val RSS_ITEM = "rss_item"
    }

    private val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item: Item? = intent.getParcelableExtra<Item>(RSS_ITEM) ?: return

        titleTextView.text = item?.title
        itemInfoTextView.text = item?.pubDate
        descriptionTextView.text = item?.description

        item?.media?.let {
            if (it.type.contains("image")) {
                Glide.with(itemImageView)
                    .load(it.url)
                    .into(itemImageView)
            }
        }

        visitButton.clicks().throttleFirst(600, TimeUnit.MILLISECONDS)
            .subscribe({
                item?.let {
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra(ITEM_URL, item.link)
                    startActivity(intent)
                }
            }, { e ->
                Log.d(ItemDetailActivity::class.java.name, "click button failed : ", e)
            }).addTo(disposeBag)

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
