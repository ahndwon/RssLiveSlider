package xyz.thingapps.rssliveslider.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_spinner.view.*
import xyz.thingapps.rssliveslider.R


class MultiSelectSpinnerAdapter(context: Context, private val list: List<String>) :
    ArrayAdapter<String>(context, R.layout.item_spinner, R.id.spinnerText, list) {

    var selectSet: MutableSet<String> = list.toSet().toMutableSet()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )

        view.spinnerText.text = getItem(position)


        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )


        view.spinnerText.text = getItem(position)

        if (selectSet.contains(view.spinnerText.text)) {
            view.spinnerCheckBox.isChecked = true

            println("contains" + view.spinnerText.text)
            println(selectSet)
        }

        view.spinnerText.setOnClickListener {
            view.spinnerCheckBox.isChecked = !view.spinnerCheckBox.isChecked
        }

        view.spinnerCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectSet.add(view.spinnerText.text.toString())

            } else {
                selectSet.remove(view.spinnerText.text.toString())

            }

            if (selectSet.contains("All RSS")) {
                selectSet = list.toSet().toMutableSet()
            }
        }

        return view
    }

}

