package xyz.thingapps.rssliveslider.utils

import android.content.Context
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.module.AppGlideModule
import xyz.thingapps.rssliveslider.models.GlideSize
import java.io.File
import java.io.IOException

@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.prepend(File::class.java, BitmapFactory.Options::class.java, BitmapSizeDecoder())
        registry.register(
            BitmapFactory.Options::class.java,
            GlideSize::class.java,
            OptionsSizeResourceTranscoder()
        )

    }

    class BitmapSizeDecoder : ResourceDecoder<File, BitmapFactory.Options> {
        @Throws(IOException::class)
        override fun handles(file: File, options: Options): Boolean {
            return true
        }

        override fun decode(
            file: File,
            width: Int,
            height: Int,
            options: Options
        ): Resource<BitmapFactory.Options>? {
            val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, bmOptions)
            return SimpleResource(bmOptions)
        }
    }

    class OptionsSizeResourceTranscoder : ResourceTranscoder<BitmapFactory.Options, GlideSize> {
        override fun transcode(
            resource: Resource<BitmapFactory.Options>,
            options: Options
        ): Resource<GlideSize> {
            val bmOptions = resource.get()
            val size = GlideSize(bmOptions.outWidth, bmOptions.outHeight)
            return SimpleResource(size)
        }
    }

}