package xyz.thingapps.rssliveslider.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.dialog_filters.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel


class FilterDialogFragment : DialogFragment() {

    private lateinit var viewModel: RssViewModel


    companion object {
        const val TAG = "FilterDialogFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_filters, container, false)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(RssViewModel::class.java)
        }

        val titleList = viewModel.castList.map {
            it.title
        }

        val sortList = context?.resources?.getStringArray(R.array.sort_by)?.toList()

        view.channelSpinner.adapter = setArrayAdapter(titleList)
        view.sortSpinner.adapter = sortList?.let { setArrayAdapter(it) }


        view.cancelButton.setOnClickListener {
            dismiss()
        }

        view.applyButton.setOnClickListener {
            //            viewModel.castListPublisher.onNext(
//            )
            dismiss()
        }


        return view
    }

    private fun setArrayAdapter(list: List<String>): ArrayAdapter<String>? {
        val adapter = context?.let { ArrayAdapter<String>(it, R.layout.item_spinner, list) }
        adapter?.setDropDownViewResource(R.layout.item_spinner)

        return adapter
    }

}
