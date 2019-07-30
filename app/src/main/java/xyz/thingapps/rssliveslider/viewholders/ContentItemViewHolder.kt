package xyz.thingapps.rssliveslider.viewholders

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.item_content.view.*
import xyz.thingapps.rssliveslider.activities.ItemDetailActivity
import xyz.thingapps.rssliveslider.models.Item
import java.util.concurrent.TimeUnit

class ContentItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item) {
        with(view) {
            feedTitle.text = item.title
            feedTime.text = item.pubDate

            item.media?.let { media ->
                if (media.type.contains("image")) {
                    Glide.with(feedImageView)
                        .load(media.url)
                        .into(feedImageView)
                }
            }

            this.clicks()
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe({
                    val intent = Intent(context, ItemDetailActivity::class.java)
                    intent.putExtra(ItemDetailActivity.RSS_ITEM, item)
                    context.startActivity(intent)
                }, { e ->
                    Log.d(ContentItemViewHolder::class.java.name, "view click failed : ", e)
                })
        }
    }
}

