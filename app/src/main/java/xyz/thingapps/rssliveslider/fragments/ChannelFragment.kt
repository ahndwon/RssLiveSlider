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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_channel.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.ItemListAdapter
import xyz.thingapps.rssliveslider.api.dao.Cast
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel
import java.util.concurrent.TimeUnit


class ChannelFragment : Fragment() {
    private var autoPlayDisposable: Disposable? = null
    private var progressDisposable: Disposable? = null
    private val disposeBag = CompositeDisposable()
    private lateinit var viewModel: RssViewModel
    private var index = -1

    companion object {
        const val TAG = "ChannelFragment"
        const val FRAGMENT_TITLE = "fragment_title"
        const val FRAGMENT_INDEX = "fragment_index"

        fun newInstance(title: String, index: Int): ChannelFragment {
            return ChannelFragment().apply {
                val bundle = Bundle()
                bundle.putString(FRAGMENT_TITLE, title)
                bundle.putInt(FRAGMENT_INDEX, index)
                arguments = bundle
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(RssViewModel::class.java)
        }
        val adapter = ItemListAdapter()
        view?.recyclerView?.adapter = adapter

        setupItems(adapter, viewModel.castList[index])

        viewModel.castListPublisher.observeOn(AndroidSchedulers.mainThread())
            .subscribe({ castList ->
                setupItems(adapter, castList[index])
            }, { e ->
                Log.d(TAG, "e : ", e)
            })
            .addTo(disposeBag)
    }

    private fun setupItems(adapter: ItemListAdapter, cast: Cast) {
        view?.fragmentTitle?.text = cast.title
        adapter.items = cast.items
        adapter.notifyDataSetChanged()
        view?.let { view ->
            autoScroll(view.recyclerView, view.slideProgressBar, adapter.items.size, 2000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channel, container, false)

        val title = arguments?.getString(FRAGMENT_TITLE)
        index = arguments?.getInt(FRAGMENT_INDEX, 0) ?: -1

        view.fragmentTitle.text = title

        setupRecyclerView(view.recyclerView)

        return view
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            this.layoutManager =
                LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)

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
                                autoPlayDisposable?.dispose()
                                progressDisposable?.dispose()
                                view?.slideProgressBar?.visibility = View.GONE
                            }
                        }
                    })
                }
            }
        }

        PagerSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
        disposeBag.dispose()
    }

    private fun autoScroll(
        recyclerView: RecyclerView,
        progressBar: ProgressBar,
        listSize: Int,
        intervalInMillis: Long
    ) {
        autoPlayDisposable?.let {
            if (!it.isDisposed) return
        }

        progressDisposable?.let {
            if (!it.isDisposed) return
        }

        autoPlayDisposable = Flowable.interval(intervalInMillis, TimeUnit.MILLISECONDS)
            .map { (it + 1) % listSize }
            .onBackpressureBuffer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                recyclerView.smoothScrollToPosition(it.toInt())
            }

        progressDisposable = Flowable.interval(intervalInMillis / 100, TimeUnit.MILLISECONDS)
            .map { it % 100 }
            .onBackpressureBuffer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progressBar.progress = it.toInt()
            }
    }

    private fun stopAutoScroll() {
        autoPlayDisposable?.let(Disposable::dispose)
        progressDisposable?.let(Disposable::dispose)
    }

}