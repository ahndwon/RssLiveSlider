package xyz.thingapps.rssliveslider.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import xyz.thingapps.rssliveslider.api.provideJSoupApi
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.models.GlideSize
import xyz.thingapps.rssliveslider.models.Item
import xyz.thingapps.rssliveslider.models.Media
import xyz.thingapps.rssliveslider.viewholders.ChannelItemViewHolder
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel

class CastJSoupParser(private val disposeBag: CompositeDisposable) {

    private val sizeOptions by lazy {
        RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }


    fun parseCastList(context: Context, castList: List<Cast>) {
        castList.forEach { cast ->
            cast.items?.forEach { item ->
                if (item.media == null) {
                    item.link?.let { link ->
                        parseLink(context, link, item)
                    }
                }
            }
        }
    }

    fun parseLink(
        context: Context,
        link: String,
        item: Item,
        onGetMax: ((String) -> Unit)? = null
    ) {
        provideJSoupApi().getDocument(link)
            .subscribe({ document ->
                val urlList =
                    document.select("img").map { element ->
                        when {
                            element.hasAttr("src") -> element.attr("src")
                            element.hasAttr("data-src") -> element.attr("data-src")
                            else -> ""
                        }
                    }
                getMaxImage(context, urlList, item, onGetMax)
            }, { e ->
                Log.i(
                    ChannelItemViewHolder::class.java.name,
                    "getDocument error : ",
                    e
                )
            }).addTo(disposeBag)
    }


    private fun getMaxImage(
        context: Context,
        urlList: List<String>,
        item: Item,
        onGetMax: ((String) -> Unit)? = null
    ) {
        Observable.zip(urlList.map {
            getGlideSize(context, it)
        }) { data ->
            data.map {
                it as GlideSize
            }.toList().maxBy { size ->
                size.width * size.height
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ max ->
                max?.let {
                    onGetMax?.invoke(max.url)
                    item.media = Media(max.url, "image")
                }
            }, { e ->
                Log.d(
                    RssViewModel::class.java.name,
                    "getGlideSize failed : ",
                    e
                )
            }).addTo(disposeBag)
    }


    private fun getGlideSize(context: Context, url: String): Observable<GlideSize> {
        return Observable.create { emitter ->
            Glide.with(context)
                .`as`(GlideSize::class.java)
                .apply(sizeOptions)
                .load(url)
                .into(object : SimpleTarget<GlideSize>() {
                    override fun onResourceReady(
                        resource: GlideSize,
                        transition: Transition<in GlideSize>?
                    ) {
                        emitter.onNext(resource.apply {
                            this.url = url
                        })
                        emitter.onComplete()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Log.d("GlideImage", "image size error : $errorDrawable")
                    }
                })
        }
    }

    fun dispose() {
        disposeBag.dispose()
    }
}
