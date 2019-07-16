package xyz.thingapps.rssliveslider.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.FragmentListAdapter
import xyz.thingapps.rssliveslider.models.RssItem
import xyz.thingapps.rssliveslider.utils.Dummies

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val adapter = FragmentListAdapter(childFragmentManager)

        adapter.fragments = listOf(
                ChannelFragment.newInstance("Channel", Dummies.rssItems)
//                ChannelFragment.newInstance("Channel", Dummies.rssItems),
//                ChannelFragment.newInstance("Channel", Dummies.rssItems),
//                ChannelFragment.newInstance("Channel", Dummies.rssItems),
//                ChannelFragment.newInstance("Channel", Dummies.rssItems),
//                ChannelFragment.newInstance("Channel", Dummies.rssItems)
        )

        view.homeRecyclerView.adapter = adapter
        view.homeRecyclerView.layoutManager = LinearLayoutManager(view.context)

        return view
    }


}
