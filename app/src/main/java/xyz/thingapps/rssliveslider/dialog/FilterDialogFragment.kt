package xyz.thingapps.rssliveslider.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.dialog_filter.view.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.utils.MultiSelectSpinnerAdapter
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel


class FilterDialogFragment : DialogFragment() {

    private lateinit var viewModel: RssViewModel

    companion object {
        const val TAG = "FilterDialogFragment"
        const val ADD_ASCENDING = "Add Ascending"
        const val ADD_DESCENDING = "Add Descending"
        const val TITLE_ASCENDING = "Title Ascending"
        const val TITLE_DESCENDING = "Title Descending"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_filter, container, false)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(RssViewModel::class.java)
        }

        val sortList = context?.resources?.getStringArray(R.array.sort_by)?.toList()
        val filterRssList = viewModel.castList.map { it.title ?: "" }

        view.channelSpinner.adapter =
            context?.let { MultiSelectSpinnerAdapter(it, viewModel.castTitleList, filterRssList) }
        view.sortSpinner.adapter = sortList?.let {
            context?.let { context ->
                ArrayAdapter<String>(
                    context,
                    R.layout.item_spinner,
                    it
                )
            }
        }

        view.cancelButton.setOnClickListener {
            dismiss()
        }

        view.applyButton.setOnClickListener {
            val channelSpinnerAdapter = view.channelSpinner.adapter as MultiSelectSpinnerAdapter

            viewModel.filter(
                channelSpinnerAdapter.selectSet,
                view.sortSpinner.selectedItem.toString()

            )
            dismiss()
        }

        return view
    }

}
