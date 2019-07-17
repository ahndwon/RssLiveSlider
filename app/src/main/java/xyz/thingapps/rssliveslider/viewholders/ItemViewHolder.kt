package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.api.dao.Item

class ItemViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
    fun bind(rss: Item) {
//        Glide.with(view.context).load(rss.image)
//                .centerCrop()
//                .into(view.feedImageView)
    }
}