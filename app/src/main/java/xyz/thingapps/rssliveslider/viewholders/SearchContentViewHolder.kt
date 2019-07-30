package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_search_content.view.*
import xyz.thingapps.rssliveslider.models.Item

class SearchContentViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    var onClick: ((Item) -> Unit)? = null

    fun bind(item: Item) {
        Glide.with(view.feedImageView).load(item.media?.url)
            .into(view.feedImageView)
        view.feedTitle.text = item.title
        view.feedTime.text = item.pubDate
        view.setOnClickListener {
            onClick?.invoke(item)
        }
    }
}