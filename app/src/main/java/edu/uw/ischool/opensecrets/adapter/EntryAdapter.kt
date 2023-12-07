package edu.uw.ischool.opensecrets.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import edu.uw.ischool.opensecrets.R
import edu.uw.ischool.opensecrets.model.Entry
import java.text.SimpleDateFormat

class EntryAdapter(context: Context, private val entries: List<Entry>) :
    ArrayAdapter<Entry>(context, R.layout.entry_item_list_view, R.id.entry_item_title, entries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        val title: TextView = view.findViewById(R.id.entry_item_title)
        val body: TextView = view.findViewById(R.id.entry_item_body)
        val colorBox: View = view.findViewById(R.id.entry_item_color)
        val date: TextView = view.findViewById(R.id.entry_item_date)

        title.text = entries[position].title
        body.text = entries[position].text
        setBackgroundTint(entries[position].color, colorBox)
        val format = SimpleDateFormat("MMM dd, yyyy")
        date.text = format.format(entries[position].dateCreated)
        return view
    }

    private fun setBackgroundTint(color: String, view: View) {
        when (color) {
            "red" -> view.backgroundTintList = ResourcesCompat.getColorStateList(
                context.resources, R.color.red, null
            )
//                ResourcesCompat.getDrawable(context.resources, R.color.red, null)

            "blue" -> view.backgroundTintList = ResourcesCompat.getColorStateList(
                context.resources, R.color.blue, null
            )

            "green" -> view.backgroundTintList = ResourcesCompat.getColorStateList(
                context.resources, R.color.green, null
            )

            "purple" -> view.backgroundTintList = ResourcesCompat.getColorStateList(
                context.resources, R.color.purple, null
            )

            "yellow" -> view.backgroundTintList = ResourcesCompat.getColorStateList(
                context.resources, R.color.yellow, null
            )

            "orange" -> view.backgroundTintList = ResourcesCompat.getColorStateList(
                context.resources, R.color.orange, null
            )

            else -> {}
        }
    }
}