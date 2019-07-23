package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.api.dao.Item

class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item) {
        with(view) {
            feedTitle.text = item.title
            feedTime.text = item.pubDate
            feedDescription.text = item.description

            item.media?.let { media ->
                if (media.type.contains("image"))
                    Glide.with(view.context).load(media.url)
                        .centerCrop()
                        .into(view.feedImageView)
            }
        }
    }
}