package xyz.thingapps.rssliveslider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.viewholders.FragmentViewHolder

class FragmentListAdapter(private val fragmentManager: FragmentManager)
    : RecyclerView.Adapter<FragmentViewHolder>() {
    var fragments: List<Fragment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fragment, parent, false)
        view.rootView.id = viewType
        return FragmentViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position + 1
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int) {
        fragmentManager.beginTransaction()
                .replace(holder.itemView.id, fragments[position])
                .commit()
    }

}