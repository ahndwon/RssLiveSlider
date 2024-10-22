package xyz.thingapps.rssliveslider.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_channel.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.activities.AllContentsActivity
import xyz.thingapps.rssliveslider.activities.AllContentsActivity.Companion.RSS_CAST
import xyz.thingapps.rssliveslider.adapters.ChannelItemListAdapter
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.viewholders.ChannelItemViewHolder
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel
import java.util.concurrent.TimeUnit


class ChannelFragment : Fragment() {
    private var autoPlayDisposable: Disposable? = null
    private var progressDisposable: Disposable? = null
    private val disposeBag = CompositeDisposable()
    private val currentFragmentDisposeBag = CompositeDisposable()
    private var animationDisposeBag = CompositeDisposable()
    private var currentFeed: Int = 0

    private lateinit var viewModel: RssViewModel
    private var index = -1

    companion object {
        const val TAG = "ChannelFragment"
        const val FRAGMENT_TITLE = "fragment_title"
        const val FRAGMENT_INDEX = "fragment_index"
        const val AUTO_SCROLL_DURATION = 10000L
        const val PROGRESS_MAX = 1000

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

        val adapter = ChannelItemListAdapter(0, tag?.toInt() ?: 0)
        view?.recyclerView?.adapter = adapter

        if (viewModel.castList.isNotEmpty())
            setupItems(adapter, viewModel.castList[index])

        view?.recyclerView?.let { setupRecyclerView(it) }

        viewModel.castListPublisher.observeOn(AndroidSchedulers.mainThread())
            .subscribe({ castList ->
                view?.let {
                    if (index < castList.size)
                        setupItems(adapter, castList[index])
                }
            }, { e ->
                e.printStackTrace()
            })
            .addTo(disposeBag)

        if (tag?.toInt() == 0) {
            view?.let { view ->
                autoScroll(
                    view.recyclerView,
                    view.slideProgressBar,
                    adapter.items.size,
                    AUTO_SCROLL_DURATION
                )
            }
        }

        viewModel.currentFragmentPublisher.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                adapter.currentChannel = it

                if (it == tag?.toInt()) {
                    view?.let { view ->
                        autoScroll(
                            view.recyclerView,
                            view.slideProgressBar,
                            adapter.items.size,
                            AUTO_SCROLL_DURATION
                        )

                        val currentFeed =
                            (view.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        (view.recyclerView.findViewHolderForLayoutPosition(currentFeed) as ChannelItemViewHolder).animate()
                    }
                } else {
                    view?.let { view ->
                        stopAutoScroll(view)
                        currentFeed =
                            (view.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    }
                }

            }, { e ->
                e.printStackTrace()
            }).addTo(currentFragmentDisposeBag)

        view?.allContentsButton?.clicks()?.subscribe({
            val intent = Intent(this@ChannelFragment.context, AllContentsActivity::class.java)
            intent.putExtra(RSS_CAST, viewModel.castList[index])
            startActivity(intent)
        }, { e ->
            e.printStackTrace()
        })?.addTo(disposeBag)
    }

    private fun setupItems(adapter: ChannelItemListAdapter, cast: Cast) {
        view?.fragmentTitle?.text = cast.title
        adapter.items = cast.items ?: emptyList()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channel, container, false)

        val title = arguments?.getString(FRAGMENT_TITLE)
        index = arguments?.getInt(FRAGMENT_INDEX, 0) ?: -1

        view.fragmentTitle.text = title

        return view
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager =
            LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.adapter?.itemCount?.let { itemCount ->
            if (itemCount < 2) return@let

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

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
                        view?.let {
                            stopAutoScroll(it)
                            currentFeed =
                                (it.recyclerView?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() + 1
                        }
                    }
                }
            })
        }

        PagerSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.let { stopAutoScroll(it) }
        disposeBag.dispose()
    }

    private fun autoScroll(
        recyclerView: RecyclerView,
        progressBar: ProgressBar,
        listSize: Int,
        intervalInMillis: Long
    ) {
        autoPlayDisposable?.let { if (!it.isDisposed) return }
        progressDisposable?.let { if (!it.isDisposed) return }


        autoPlayDisposable = Flowable.interval(intervalInMillis, TimeUnit.MILLISECONDS)
            .map { (it + 1 + currentFeed) % listSize }
            .onBackpressureBuffer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                recyclerView.smoothScrollToPosition(it.toInt())

            }, { e ->
                e.printStackTrace()
            }).addTo(animationDisposeBag)

        progressDisposable =
            Flowable.interval(intervalInMillis / PROGRESS_MAX, TimeUnit.MILLISECONDS)
                .map { it % PROGRESS_MAX }
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    progressBar.progress = it.toInt()
                    progressBar.visibility = View.VISIBLE

                }, { e ->
                    e.printStackTrace()
                }).addTo(animationDisposeBag)

    }

    private fun stopAutoScroll(view: View) {
        animationDisposeBag.dispose()
        animationDisposeBag = CompositeDisposable()

        view.slideProgressBar.visibility = View.INVISIBLE

    }

}