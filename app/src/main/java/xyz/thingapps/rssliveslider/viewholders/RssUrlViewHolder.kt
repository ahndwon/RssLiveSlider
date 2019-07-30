package xyz.thingapps.rssliveslider.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rss_url.view.*
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.models.RssUrl

class RssUrlViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    var onDeleteClick: ((Int) -> Unit)? = null

    fun bind(item: RssUrl, position: Int) {
        with(view) {
            rssTitle.text = item.title
            rssUrl.text = item.url
            rssCreatedAt.text = getAddedAt(item.createdAt)
            deleteButton.setOnClickListener {
                onDeleteClick?.invoke(position)
            }
        }
    }

    private fun getAddedAt(createdAt: Long): String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val date = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ).format(formatter)
        return view.context.getString(R.string.rss_add_at, date)
    }
}

