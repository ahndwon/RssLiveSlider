package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_basic.view.*
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.models.RssItem
import xyz.thingapps.rssliveslider.viewholders.ItemViewHolder

class ItemListAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    var items: List<RssItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

}