package xyz.thingapps.rssliveslider.viewholders

import android.media.MediaPlayer
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_feed.view.*
import xyz.thingapps.rssliveslider.api.dao.Item
import xyz.thingapps.rssliveslider.utils.ThumbnailTask
import java.util.concurrent.TimeUnit

class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item) {
        with(view) {
            feedTitle.text = item.title
            feedTime.text = item.pubDate
            feedDescription.text = item.description

            item.media?.let { media ->
                if (media.type.contains("image")) {
                    Glide.with(view.context).load(media.url)
                        .centerCrop()
                        .into(view.feedImageView)
                    view.feedVideoView.visibility = View.GONE
                }

                var mediaPlayer = MediaPlayer()

                if (media.type.contains("video")) {
                    ThumbnailTask(media.url).execute(view.feedImageView)

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
                                val path = media.url
                                mediaPlayer.apply {
                                    setDataSource(path)
                                    setVolume(0f, 0f)
                                    setDisplay(p0)
                                    setOnPreparedListener {
                                        it.start()

                                        Observable.timer(600, TimeUnit.MILLISECONDS)
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
                                e.message?.let {
                                    Log.e("video", it)
                                }
                            }
                        }

                    }

                    feedVideoView.holder.addCallback(callback)

                }


            }
        }
    }
}