package xyz.thingapps.rssliveslider.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.get
import kotlinx.android.synthetic.main.item_spinner_multi.view.*
import xyz.thingapps.rssliveslider.R


class MultiSelectSpinnerAdapter(context: Context, list: List<String>, filterList: List<String>) :
    ArrayAdapter<String>(context, R.layout.item_spinner_multi, R.id.spinnerText, list) {

    var selectSet: MutableSet<String> = filterList.toSet().toMutableSet()
    private lateinit var getViewParent: ViewGroup

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner_multi,
            parent,
            false
        )

        getViewParent = parent

        view.spinnerCheckBox.visibility = View.INVISIBLE
        view.spinnerText.text = setSpinnerText()

        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner_multi,
            parent,
            false
        )

        view.spinnerText.text = getItem(position)
        view.spinnerCheckBox.isChecked = selectSet.contains(view.spinnerText.text)

        view.spinnerText.setOnClickListener {
            view.spinnerCheckBox.isChecked = !view.spinnerCheckBox.isChecked
        }

        view.spinnerCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectSet.add(view.spinnerText.text.toString())

            } else {
                selectSet.remove(view.spinnerText.text.toString())
            }
            getViewParent[0].spinnerText.text = setSpinnerText()
        }

        return view
    }

    private fun setSpinnerText(): String {

        return if (selectSet.size == count) {
            context.getString(R.string.all_rss)
        } else
            selectSet.last() + context.getString(R.string.ellipsis)
    }

}

