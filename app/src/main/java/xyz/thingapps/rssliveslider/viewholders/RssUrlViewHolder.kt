package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rss_url.view.*
import xyz.thingapps.rssliveslider.models.RssUrl

class RssUrlViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: RssUrl) {
        with(view) {
            rssTitle.text = item.title
            rssUrl.text = item.url
            rssCreatedAt.text = item.createdAt.toString()
        }
    }
}

