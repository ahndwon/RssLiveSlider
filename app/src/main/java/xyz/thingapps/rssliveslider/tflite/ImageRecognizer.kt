package xyz.thingapps.rssliveslider.tflite

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.SimpleTarget
import io.reactivex.Observable
import java.io.IOException

class ImageRecognizer(private val context: Context) {
    private var classifier: Classifier? = null
    private var rgbFrameBitmap: Bitmap? = null
    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null

    private fun processImage(bitmap: Bitmap, pixels: IntArray): List<Classifier.Recognition> {

        rgbFrameBitmap?.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val croppedBitmap =
            classifier?.let {
                Bitmap.createBitmap(
                    it.imageSizeX,
                    it.imageSizeY,
                    Bitmap.Config.ARGB_8888
                )
            }
        val canvas = croppedBitmap?.let { Canvas(it) }
        rgbFrameBitmap?.let { rgbFrameBitmap ->
            frameToCropTransform?.let { frameToCropTransform ->
                canvas?.drawBitmap(
                    rgbFrameBitmap,
                    frameToCropTransform, null
                )
            }
        }

        return classifier?.recognizeImage(croppedBitmap)?.toList() ?: emptyList()

    }

    fun getRecognitions(url: String): Observable<List<Classifier.Recognition>> {
        return Observable.create { emitter ->
            getBitmap(url).subscribe {
                Glide.with(context).asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .load(url)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                        ) {
                            val results = recognizeImage(resource)
                            emitter.onNext(results)
                            emitter.onComplete()
                        }
                    })
            }
        }
    }

    private fun recognizeImage(resource: Bitmap): List<Classifier.Recognition> {
        val pixels = IntArray(resource.width * resource.height)
        resource.getPixels(pixels, 0, resource.width, 0, 0, resource.width, resource.height)
        rgbFrameBitmap =
            Bitmap.createBitmap(resource.width, resource.height, Bitmap.Config.ARGB_8888)

        frameToCropTransform = classifier?.let {
            ImageUtils.getTransformationMatrix(
                resource.width,
                resource.height,
                it.imageSizeX,
                it.imageSizeY,
                0,
                true
            )
        }
        cropToFrameTransform = Matrix()
        frameToCropTransform?.invert(cropToFrameTransform)
        return processImage(resource, pixels)
    }

    private fun getBitmap(url: String): Observable<Bitmap> {
        return Observable.create { emitter ->
            Glide.with(context).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        emitter.onNext(resource)
                        emitter.onComplete()
                    }
                })

        }
    }

    fun setClassifier(
        activity: Activity
    ) {
        try {
            classifier =
                Classifier.create(activity, Classifier.Model.QUANTIZED, Classifier.Device.CPU, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


