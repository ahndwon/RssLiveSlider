package xyz.thingapps.rssliveslider.viewholders

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.activities.ItemDetailActivity
import xyz.thingapps.rssliveslider.api.provideJSoupApi
import xyz.thingapps.rssliveslider.models.Item
import xyz.thingapps.rssliveslider.models.Media
import xyz.thingapps.rssliveslider.utils.PaddingBackgroundColorSpan
import xyz.thingapps.rssliveslider.utils.ThumbnailTask
import java.util.concurrent.TimeUnit

class ChannelItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
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

    private var isVideo = false

    fun bind(item: Item, position: Int, itemCount: Int) {
        with(view) {

            dispose()

            var description = item.description

            item.description?.let { itemDescription ->
                if (itemDescription.startsWith('<')) {
                    description = itemDescription.substringAfterLast(">").trimStart()

                    if (itemDescription.contains("src=\"")) {
                        item.media = Media(
                            parseImageUrl(itemDescription),
                            "image"
                        )
                    }
                }
            }

            feedDescription.visibility = View.INVISIBLE
            feedTitle.text = item.title
            feedTime.text = item.pubDate

            description?.let { setupDescription(it, position, itemCount) }

            item.media?.let { media ->
                if (media.type.contains("image")) {
                    showFeedImage(media.url)
                    view.feedVideoView.visibility = View.GONE
                }

                if (media.type.contains("video")) {
                    playVideo(media.url)
                }
            }

            if (item.media == null) {
                item.link?.let { link ->
                    provideJSoupApi().getDocument(link)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ document ->
                            val urlList = document.select("img").map { element ->
                                when {
//                                    element.hasAttr("src") -> element.attr("src")
                                    element.hasAttr("data-src") -> element.attr("data-src")
                                    else -> ""
                                }
                            }

                            Log.i(
                                ChannelItemViewHolder::class.java.name,
                                "urlList : $urlList"
                            )

                            urlList.max()?.let { longestUrl ->
                                showFeedImage(longestUrl)

                                item.media = Media(
                                    parseImageUrl(longestUrl),
                                    "image"
                                )

                                this.setOnClickListener {
                                    val intent = Intent(context, ItemDetailActivity::class.java)
                                    intent.putExtra(ItemDetailActivity.RSS_ITEM, item)
                                    context.startActivity(intent)
                                }

                                Log.i(
                                    ChannelItemViewHolder::class.java.name,
                                    "longestUrl : $longestUrl"
                                )
                            }


                        }, { e ->
                            Log.i(ChannelItemViewHolder::class.java.name, "getDocument error : ", e)
                        })
                }
            }

            setOnClickListener {
                val intent = Intent(context, ItemDetailActivity::class.java)
                intent.putExtra(ItemDetailActivity.RSS_ITEM, item)
                context.startActivity(intent)
            }
        }
    }

    private fun showFeedImage(url: String) {
        Glide.with(view.context).load(url)
            .centerCrop()
            .into(view.feedImageView)
    }

    private fun setupDescription(description: String, position: Int, itemCount: Int) {
        view.feedDescription.text = description
        val feedPositionText = "${position + 1}/$itemCount"
        view.feedPosition.text = feedPositionText

        view.feedDescription.setShadowLayer(padding.toFloat(), 0.0F, 0.0F, 0)
        view.feedDescription.setPadding(padding, padding - 10, padding, padding - 10)

        Single.just(view.feedDescription)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.feedDescription.text =
                    view.feedDescription.createEllipsis().createSpan(paddingBackgroundColorSpan)
            }, { e ->
                e.printStackTrace()
            }).addTo(disposeBag)
    }

    private fun playVideo(url: String) {
        isVideo = true

        var mediaPlayer = MediaPlayer()

        ThumbnailTask(url).execute(view.feedImageView)

        val callback = object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mediaPlayer.release()
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
                mediaPlayer = MediaPlayer()
                mediaPlayer.reset()

                try {
                    mediaPlayer.apply {
                        setDataSource(url)
                        setVolume(0f, 0f)
                        setDisplay(p0)
                        setOnPreparedListener {
                            it.start()

                            Observable.timer(900, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    view.feedImageView.visibility = View.GONE
                                }, { e ->
                                    e.printStackTrace()
                                })
                        }

                        prepareAsync()
                    }

                } catch (e: Exception) {
                    Log.i(ChannelItemViewHolder::class.java.name, "video play failed ", e)
                }
            }

        }

        view.feedVideoView.holder.addCallback(callback)
    }

    private fun parseImageUrl(source: String): String {
        return source.split("src=\"")[1].split("\"")[0]
    }

    fun animate() {
        view.feedDescription.visibility = View.VISIBLE
        view.feedDescription.createAnimation(view.context, R.anim.animation_feed_description, 3000)
        if (!isVideo) view.feedImageView.createAnimation(
            view.context,
            R.anim.animation_feed_image,
            0
        )
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

