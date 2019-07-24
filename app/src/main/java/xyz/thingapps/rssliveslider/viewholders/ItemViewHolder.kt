package xyz.thingapps.rssliveslider.viewholders

import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.api.dao.Item
import xyz.thingapps.rssliveslider.utils.PaddingBackgroundColorSpan


class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item) {
        with(view) {
            feedTitle.text = item.title
            feedTime.text = item.pubDate
            feedDescription.text = item.description

            item.media?.let { media ->
                if (media.type.contains("image"))
                    Glide.with(view.context).load(media.url)
                        .centerCrop()
                        .into(view.feedImageView)
            }

            val padding = 20
            val descriptionBackgroundColor =
                ResourcesCompat.getColor(
                    resources,
                    xyz.thingapps.rssliveslider.R.color.colorTransparentBlack,
                    null
                )
            val paddingBackgroundColorSpan =
                PaddingBackgroundColorSpan(descriptionBackgroundColor, padding)

            feedDescription.setShadowLayer(padding.toFloat(), 0.0F, 0.0F, 0)
            feedDescription.setPadding(padding, padding - 10, padding, padding - 10)


            feedDescription.text =
                feedDescription.text.toString().createSpan(paddingBackgroundColorSpan)


            val viewTreeObserver = feedDescription.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    feedDescription.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    if (feedDescription.lineCount > feedDescription.maxLines) {
                        val endOfLastLine =
                            feedDescription.layout.getLineEnd(feedDescription.maxLines - 1)
                        val spanText =
                            feedDescription.text.subSequence(
                                0,
                                endOfLastLine - 3
                            ).toString() + " ..."


                        feedDescription.text = spanText.createSpan(paddingBackgroundColorSpan)

                    }

                }
            })
        }
    }

    fun String.createSpan(spans: Any): Spannable {
        val span = SpannableString(this)

        span.setSpan(
            spans,
            0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span
    }
}
