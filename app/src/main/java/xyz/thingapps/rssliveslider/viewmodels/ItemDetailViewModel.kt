package xyz.thingapps.rssliveslider.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.tflite.ImageRecognizer
import xyz.thingapps.rssliveslider.utils.CastJSoupParser
import java.util.*

class ItemDetailViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var castList: ArrayList<Cast>
    private val disposeBag = CompositeDisposable()
    val imageRecognizer = ImageRecognizer(getApplication())
    val jSoupParser = CastJSoupParser(disposeBag)

    fun setImageRecognitions() {
        castList.forEach { cast ->
            cast.items?.forEach { item ->
                if (item.media?.type?.contains("image") == true) {
                    item.media?.url?.let { url ->
                        imageRecognizer.getRecognitions(url)
//                            .subscribeOn(Schedulers.io())
                            .subscribe({ results ->
                                val label = results.maxBy {
                                    it.confidence
                                }?.title ?: ""

                                Log.d("ImageRecognizer", "label : $label")

                                if (label.isNotBlank())
                                    item.recognition = label
                            }, { e ->
                                Log.e("ImageRecognizer", "recognize image failed : ", e)
                            }).addTo(disposeBag)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }
}