package xyz.thingapps.rssliveslider.viewholders

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.api.dao.Item
import xyz.thingapps.rssliveslider.utils.PaddingBackgroundColorSpan


class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private var disposeBag = CompositeDisposable()
    private val padding = 20
    private val descriptionBackgroundColor =
        ResourcesCompat.getColor(
            view.resources,
            R.color.colorTransparentBlack,
            null
        )
    private val paddingBackgroundColorSpan =
        PaddingBackgroundColorSpan(descriptionBackgroundColor, padding)


    fun bind(item: Item, position: Int, itemCount: Int) {
        with(view) {

            dispose()

            feedDescription.visibility = View.INVISIBLE
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

            feedDescription.setShadowLayer(padding.toFloat(), 0.0F, 0.0F, 0)
            feedDescription.setPadding(padding, padding - 10, padding, padding - 10)


            Single.just(feedDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    feedDescription.text =
                        feedDescription.createEllipsis().createSpan(paddingBackgroundColorSpan)
                }, { e ->
                    e.printStackTrace()
                }).addTo(disposeBag)

        }
    }

    fun animate() {
        view.feedDescription.visibility = View.VISIBLE
        view.feedDescription.createAnimation(view.context, R.anim.animation_feed_description, 3000)
        view.feedImageView.createAnimation(view.context, R.anim.animation_feed_image, 0)
    }

    private fun dispose() {
        disposeBag.dispose()
        disposeBag = CompositeDisposable()
    }

    private fun String.createSpan(spans: Any): Spannable {
        val span = SpannableString(this)

        span.setSpan(
            spans,
            0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span
    }

    private fun TextView.createEllipsis(): String {
        return if (this.lineCount > this.maxLines) {
            val endOfLastLine = this.layout.getLineEnd(this.maxLines - 1)

            this.text.subSequence(0, endOfLastLine - 6).toString() + " ..."

        } else
            this.text.toString()
    }

    private fun View.createAnimation(context: Context, resource: Int, startOffset: Long) {
        this.animation?.let {
            if (!this.animation.hasEnded() || this.animation.hasStarted()) {
                return
            }
        }
        val animation = AnimationUtils.loadAnimation(context, resource)
        animation.startOffset = startOffset

        this.startAnimation(animation)

    }

}

