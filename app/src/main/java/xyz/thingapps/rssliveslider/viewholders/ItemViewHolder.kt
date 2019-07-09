package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.models.RssItem

class ItemViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
    fun bind(rss : RssItem) {
        Glide.with(view.context).load(rss.image)
                .centerCrop()
                .into(view.feedImageView)
    }
}