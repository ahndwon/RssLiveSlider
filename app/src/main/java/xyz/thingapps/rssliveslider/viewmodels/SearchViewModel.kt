package xyz.thingapps.rssliveslider.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.tflite.ImageRecognizer
import java.util.*

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var castList: ArrayList<Cast>
    private val disposeBag = CompositeDisposable()
    val imageRecognizer = ImageRecognizer(getApplication())

    fun setImageRecognitions() {
        castList.forEach { cast ->
            cast.items?.forEach { item ->
                if (item.media?.type?.contains("image") == true) {
                    item.media?.url?.let { url ->
                        imageRecognizer.getRecognitions(url)
                            .subscribeOn(Schedulers.io())
                            .subscribe({ results ->
                                val label = results.maxBy {
                                    it.confidence
                                }?.title ?: ""

                                if (label.isNotBlank())
                                    item.recognition = label
                            }, { e ->
                                e.printStackTrace()
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