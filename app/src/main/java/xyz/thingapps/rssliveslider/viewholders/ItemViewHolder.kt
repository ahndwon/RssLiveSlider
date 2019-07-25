package xyz.thingapps.rssliveslider.viewholders

import android.text.Spannable
import android.text.SpannableString
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.api.dao.Item
import xyz.thingapps.rssliveslider.utils.PaddingBackgroundColorSpan


class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item, position: Int, itemCount: Int) {

        with(view) {
            feedTitle.text = item.title
            feedTime.text = item.pubDate
            feedDescription.text = item.description

            val feedPositionText = "${position + 1}/$itemCount"
            feedPosition.text = feedPositionText

            item.media?.let { media ->
                if (media.type.contains("image"))
                    Glide.with(context).load(media.url)
                        .centerCrop()
                        .into(feedImageView)
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

            var spanText = feedDescription.text

            Single.just(feedDescription).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.lineCount > it.maxLines) {
                        val endOfLastLine =
                            it.layout.getLineEnd(it.maxLines - 1)

                        spanText =
                            it.text.subSequence(
                                0,
                                endOfLastLine - 3
                            ).toString() + " ..."

                    }

                    it.text = spanText.toString().createSpan(paddingBackgroundColorSpan)

                }, {
                })
        }
    }

    private fun String.createSpan(spans: Any): Spannable {
        val span = SpannableString(this)

        span.setSpan(
            spans,
            0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span
    }
}
