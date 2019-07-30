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
import xyz.thingapps.rssliveslider.utils.MultiSelectSpinnerAdapter
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

        val sortList = context?.resources?.getStringArray(R.array.sort_by)?.toList()
        val filterRssList = viewModel.castList.map { it.title ?: "" }
        val filterSortList = viewModel.sortList

        view.channelSpinner.adapter = setAdapter(viewModel.castTitleList, filterRssList)
        view.sortSpinner.adapter = sortList?.let { setAdapter(it, filterSortList) }


        view.cancelButton.setOnClickListener {
            dismiss()
        }

        view.applyButton.setOnClickListener {

            val channelSpinnerAdapter = view.channelSpinner.adapter as MultiSelectSpinnerAdapter
            val sortSpinnerAdapter = view.sortSpinner.adapter as MultiSelectSpinnerAdapter


            viewModel.filter(
                channelSpinnerAdapter.selectSet,
                sortSpinnerAdapter.selectSet

            )
            dismiss()
        }

        return view
    }

    private fun setAdapter(list: List<String>, filterList: List<String>): ArrayAdapter<String>? {
        return context?.let { MultiSelectSpinnerAdapter(it, list, filterList) }
    }


}
