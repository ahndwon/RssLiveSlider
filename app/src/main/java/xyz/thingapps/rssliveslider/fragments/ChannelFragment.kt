package xyz.thingapps.rssliveslider.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_channel.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.ItemListAdapter
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.models.RssItem
import xyz.thingapps.rssliveslider.utils.viewModelFactory
import xyz.thingapps.rssliveslider.viewmodels.CastViewModel
import java.util.concurrent.TimeUnit


class ChannelFragment : Fragment() {
    private var progressDispose: Disposable? = null
    private var dispose: Disposable? = null
    private lateinit var cast: Observable<Cast>
    private lateinit var viewModel: CastViewModel

    companion object {
        const val TAG = "ChannelFragment"
        const val FRAGMENT_TITLE = "fragment_title"
        const val FRAGMENT_ITEMS = "fragment_items"

        fun newInstance(title: String, items: ArrayList<RssItem>): ChannelFragment {
            return ChannelFragment().apply {
                val bundle = Bundle()
                bundle.putString(FRAGMENT_TITLE, title)
                bundle.putParcelableArrayList(FRAGMENT_ITEMS, items)
                arguments = bundle
            }
        }

        fun newInstance(title: String, cast: Observable<Cast>): ChannelFragment {
            return ChannelFragment().apply {
                val bundle = Bundle()
                bundle.putString(FRAGMENT_TITLE, title)
                this.cast = cast
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory {
                CastViewModel(cast)
            }).get(CastViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channel, container, false)
        val title = arguments?.getString(FRAGMENT_TITLE)
        val images = arguments?.getParcelableArrayList<RssItem>(FRAGMENT_ITEMS) ?: ArrayList()

        Log.d(TAG, "images : $images")

        view.fragmentTitle.text = title
        val adapter = ItemListAdapter()
        adapter.items = images
        val snapHelper = PagerSnapHelper()

        view.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

            this.adapter?.itemCount?.let { itemCount ->
                if (itemCount > 1) {
                    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            val layoutManager = this@apply.layoutManager as LinearLayoutManager

                            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                            if (firstVisibleItem > 0 && firstVisibleItem % itemCount == 0) {
                                recyclerView.scrollToPosition(0)

                            }
                        }

                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)


                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                dispose?.dispose()
                                progressDispose?.dispose()
                                view.slideProgressBar.visibility = View.GONE
                            }
                        }
                    })
                }
            }
        }
        snapHelper.attachToRecyclerView(view.recyclerView)
        autoScroll(view.recyclerView, view.slideProgressBar, images.size, 2000)


        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
    }

    private fun autoScroll(
        recyclerView: RecyclerView,
        progressBar: ProgressBar,
        listSize: Int,
        intervalInMillis: Long
    ) {
        dispose?.let {
            if (!it.isDisposed) return
        }

        progressDispose?.let {
            if (!it.isDisposed) return
        }

        dispose = Flowable.interval(intervalInMillis, TimeUnit.MILLISECONDS)
            .map { (it + 1) % listSize }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                recyclerView.smoothScrollToPosition(it.toInt())
            }

        progressDispose = Flowable.interval(intervalInMillis / 100, TimeUnit.MILLISECONDS)
            .map { it % 100 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progressBar.progress = it.toInt()
                println("progress" + it.toInt())
            }
    }

    private fun stopAutoScroll() {
        dispose?.let(Disposable::dispose)
        progressDispose?.let(Disposable::dispose)
    }

}