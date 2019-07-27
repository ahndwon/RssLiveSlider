package xyz.thingapps.rssliveslider.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        fragmentAdapter = FragmentListAdapter(childFragmentManager)
        view.homeRecyclerView.layoutManager = LinearLayoutManager(view.context)

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(RssViewModel::class.java)
            viewModel.getData()

            viewModel.channelListPublisher
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fragmentAdapter.fragments = viewModel.channelList
                    view?.homeRecyclerView?.adapter = fragmentAdapter
                }.addTo(disposeBag)
        }
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }
}
