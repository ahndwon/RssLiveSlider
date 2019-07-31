package xyz.thingapps.rssliveslider.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.widget.ImageView

class ThumbnailTask(val url: String) :
    AsyncTask<ImageView, Void, Pair<Bitmap, ImageView>>() {

    override fun doInBackground(vararg imageViews: ImageView): Pair<Bitmap, ImageView>? {

        val videoPath = url
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        return try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
            val bitmap = mediaMetadataRetriever.getFrameAtTime(1000000)
            Pair(bitmap, imageViews[0])
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            mediaMetadataRetriever?.release()
        }

    }


    override fun onPostExecute(result: Pair<Bitmap, ImageView>?) {
        super.onPostExecute(result)
        result.let {
            it?.second?.setImageBitmap(it.first)
        }
    }

}