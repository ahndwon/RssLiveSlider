package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.api.Item
import xyz.thingapps.rssliveslider.viewholders.ContentItemViewHolder

class ContentItemListAdapter :
    RecyclerView.Adapter<ContentItemViewHolder>() {
    var items: List<Item> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content, parent, false)

        return ContentItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ContentItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}