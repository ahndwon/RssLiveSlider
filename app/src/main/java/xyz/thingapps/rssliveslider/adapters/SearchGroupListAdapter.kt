package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.models.Item
import xyz.thingapps.rssliveslider.viewholders.SearchGroupViewHolder

class SearchGroupListAdapter
    : RecyclerView.Adapter<SearchGroupViewHolder>() {
    var items: List<Cast> = emptyList()
    var onItemClick: ((Item) -> Unit)? = null
    var onGroupClick: ((Cast) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_group, parent, false)

        return SearchGroupViewHolder(view).apply {
            this.onItemClick = this@SearchGroupListAdapter.onItemClick
            this.onGroupClick = this@SearchGroupListAdapter.onGroupClick
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: SearchGroupViewHolder, position: Int) {
        holder.bind(items[position])
    }
}