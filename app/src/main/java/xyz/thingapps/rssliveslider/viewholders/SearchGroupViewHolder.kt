package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_search_group.view.*
import xyz.thingapps.rssliveslider.adapters.SearchContentListAdapter
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.models.Item

class SearchGroupViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    var onItemClick: ((Item) -> Unit)? = null
    var onGroupClick: ((Cast) -> Unit)? = null

    fun bind(item: Cast) {
        val group = item.title
        val contents = item.items ?: emptyList()

        view.groupTextView.text = group
        val adapter = SearchContentListAdapter()
        onItemClick?.let {
            adapter.onClick = onItemClick
        }
        adapter.items = contents
        view.searchContentRecyclerView.adapter = adapter
        view.searchContentRecyclerView.layoutManager = LinearLayoutManager(view.context)
        view.searchContentRecyclerView.isNestedScrollingEnabled = false

        onGroupClick?.let {
            view.setOnClickListener {
                onGroupClick?.invoke(item)
            }
        }
    }
}