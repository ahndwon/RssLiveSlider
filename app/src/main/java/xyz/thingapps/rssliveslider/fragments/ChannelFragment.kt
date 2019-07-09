package xyz.thingapps.rssliveslider.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_channel.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.ItemListAdapter
import xyz.thingapps.rssliveslider.models.RssItem


class ChannelFragment : Fragment() {
    private var title: String? = null
    private var image: ArrayList<String>? = null

    companion object {
        const val TAG = "ChannelFragment"
        const val FRAGMENT_TITLE = "fragment_title"
        const val FRAGMENT_ITEMS = "fragment_items"

        fun newInstance(title: String, items: ArrayList<RssItem>) : ChannelFragment {
            return ChannelFragment().apply {
                val bundle = Bundle()
                bundle.putString(FRAGMENT_TITLE, title)
                bundle.putParcelableArrayList(FRAGMENT_ITEMS, items)
                arguments = bundle
            }
        }
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

        view.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        }

        return view
    }

}
