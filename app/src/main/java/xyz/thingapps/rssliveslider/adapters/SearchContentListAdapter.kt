package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.models.Item
import xyz.thingapps.rssliveslider.viewholders.SearchContentViewHolder

class SearchContentListAdapter : RecyclerView.Adapter<SearchContentViewHolder>() {
    var items: List<Item> = emptyList()
    var onClick: ((Item) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_content, parent, false)

        return SearchContentViewHolder(view).apply {
            this.onClick = this@SearchContentListAdapter.onClick
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SearchContentViewHolder, position: Int) {
        holder.bind(items[position])
    }
}