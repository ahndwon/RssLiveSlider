package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.models.Item
import xyz.thingapps.rssliveslider.viewholders.ChannelItemViewHolder

class ChannelItemListAdapter(var currentChannel: Int, private val tag: Int) :
    RecyclerView.Adapter<ChannelItemViewHolder>() {
    var items: List<Item> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed, parent, false)

        return ChannelItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChannelItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position, items.size)
    }

    override fun onViewAttachedToWindow(holder: ChannelItemViewHolder) {
        if (currentChannel == tag) {
            holder.animate()
        }
    }
}