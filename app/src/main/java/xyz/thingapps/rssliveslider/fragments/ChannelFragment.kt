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
import xyz.thingapps.rssliveslider.models.RssItem
import xyz.thingapps.rssliveslider.viewmodels.HomeViewModel
import java.util.concurrent.TimeUnit


class ChannelFragment : Fragment() {
    private var autoPlayDisposable: Disposable? = null
    private var progressDisposable: Disposable? = null
    private val disposeBag = CompositeDisposable()
    private lateinit var viewModel: HomeViewModel

    companion object {
        const val TAG = "ChannelFragment"
        const val FRAGMENT_TITLE = "fragment_title"
        const val FRAGMENT_ITEMS = "fragment_items"
        const val FRAGMENT_INDEX = "fragment_index"

        fun newInstance(title: String, items: ArrayList<RssItem>): ChannelFragment {
            return ChannelFragment().apply {
                val bundle = Bundle()
                bundle.putString(FRAGMENT_TITLE, title)
                bundle.putParcelableArrayList(FRAGMENT_ITEMS, items)
                arguments = bundle
            }
        }

        fun newInstance(title: String, index: Int): ChannelFragment {
            return ChannelFragment().apply {
                val bundle = Bundle()
                bundle.putString(FRAGMENT_TITLE, title)
                bundle.putInt(FRAGMENT_INDEX, index)
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = ViewModelProviders.of(
//            this,
//            viewModelFactory {
//                CastViewModel(cast)
//            }).get(CastViewModel::class.java)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(HomeViewModel::class.java)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channel, container, false)
        val title = arguments?.getString(FRAGMENT_TITLE)
        val index = arguments?.getInt(FRAGMENT_INDEX, 0) ?: 0
        val images = arguments?.getParcelableArrayList<RssItem>(FRAGMENT_ITEMS) ?: ArrayList()

        Log.d(TAG, "images : $images")
        val adapter = ItemListAdapter()

        viewModel.castList
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val cast = it[index]
                Log.d(TAG, "cast : $cast")
                Log.d(TAG, "cast.items size : ${cast.items.size}")
                view.fragmentTitle.text = cast.title
                adapter.items = cast.items
                adapter.notifyDataSetChanged()
                autoScroll(view.recyclerView, view.slideProgressBar, adapter.items.size, 2000)
            }, { e ->
                Log.d(TAG, "e : ", e)
            }).addTo(disposeBag)

//        viewModel.castList[index]
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ cast ->
//                view.fragmentTitle.text = cast.title
//                adapter.items = cast.items
//                adapter.notifyDataSetChanged()
//                autoScroll(view.recyclerView, view.slideProgressBar, adapter.items.size, 2000)
//            }, { e ->
//                Log.d(TAG, "e : ", e)
//            })
//            .addTo(disposeBag)

//        viewModel.castList.subscribe({
//            it.elementAt(index)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ cast ->
//                    Log.d(TAG, "cast : $cast")
//                    Log.d(TAG, "cast.items size : ${cast.items.size}")
//                    view.fragmentTitle.text = cast.title
//                    adapter.items = cast.items
//                    adapter.notifyDataSetChanged()
//                    autoScroll(view.recyclerView, view.slideProgressBar, adapter.items.size, 2000)
//                }, { e ->
//                    Log.d(TAG, "e : ", e)
//                })
//        }, { e ->
//            Log.d(TAG, "e : ", e)
//        }).addTo(disposeBag)

        view.fragmentTitle.text = title
        val snapHelper = PagerSnapHelper()

        view.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

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
                                view.slideProgressBar.visibility = View.GONE
                            }
                        }
                    })
                }
            }
        }
        snapHelper.attachToRecyclerView(view.recyclerView)



        return view
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

        Log.d(TAG, "listSize : $listSize")

        autoPlayDisposable = Flowable.interval(intervalInMillis, TimeUnit.MILLISECONDS)
            .map { (it + 1) % listSize }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                recyclerView.smoothScrollToPosition(it.toInt())
            }

        progressDisposable = Flowable.interval(intervalInMillis / 100, TimeUnit.MILLISECONDS)
            .map { it % 100 }
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