package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.models.RssUrl
import xyz.thingapps.rssliveslider.viewholders.RssUrlViewHolder

class UrlListAdapter :
    RecyclerView.Adapter<RssUrlViewHolder>() {
    var items: List<RssUrl> = emptyList()
    var onDeleteClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssUrlViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rss_url, parent, false)

        return RssUrlViewHolder(view).apply {
            this.onDeleteClick = this@UrlListAdapter.onDeleteClick
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RssUrlViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}