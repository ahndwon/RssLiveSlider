package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.api.dao.Item
import xyz.thingapps.rssliveslider.viewholders.ItemViewHolder

class ItemListAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    var items: List<Item> = emptyList()

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