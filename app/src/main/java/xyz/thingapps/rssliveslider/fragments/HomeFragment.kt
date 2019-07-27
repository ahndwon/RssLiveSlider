package xyz.thingapps.rssliveslider.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.FragmentListAdapter
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: RssViewModel
    private lateinit var fragmentAdapter: FragmentListAdapter
    private val disposeBag = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentAdapter = FragmentListAdapter(childFragmentManager)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(RssViewModel::class.java)
            viewModel.getData()

            viewModel.castListPublisher
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fragmentAdapter.fragments = viewModel.channelList
                    view?.homeRecyclerView?.adapter = fragmentAdapter
                }.addTo(disposeBag)
        }

        view?.homeRecyclerView?.let { setupRecyclerView(it) }

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(recyclerView.context)

            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val layoutManager = this@apply.layoutManager as LinearLayoutManager

                    val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (firstVisibleItem != -1) viewModel.currentFragmentPublisher.onNext(
                        firstVisibleItem
                    )
                }
            })
        }
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }
}
