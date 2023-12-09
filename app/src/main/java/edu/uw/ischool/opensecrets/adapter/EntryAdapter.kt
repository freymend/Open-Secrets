package edu.uw.ischool.opensecrets.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import edu.uw.ischool.opensecrets.EntryTextActivity
import edu.uw.ischool.opensecrets.R
import edu.uw.ischool.opensecrets.model.Entry
import java.text.SimpleDateFormat

/**
 * A custom array adapter for the simple list view that R.layout.entry_item_list_view.
 * @param deleteCall A callback function use to delete an item
 * @param context The current Activity's context.
 * @param entries a data list of [Entry]
 */
class EntryAdapter(
    context: Context, private val entries: List<Entry>,
    val deleteCall: (Int) -> Unit
) :
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

        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            deleteCall(position)
        }
        val editButton: ImageButton = view.findViewById(R.id.edit_button)
        editButton.setOnClickListener {
            val intent = Intent(
                context,
                EntryTextActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra(EntryTextActivity.EDIT, "true")
            intent.putExtra(EntryTextActivity.INDEX, position.toString())
            intent.putExtra(EntryTextActivity.TEXT, entries[position].text)
            intent.putExtra(EntryTextActivity.TITLE, entries[position].title)
            intent.putExtra(EntryTextActivity.COLOR, entries[position].color)
            context.startActivity(
                intent
            )
        }
        return view
    }

    /**
     * Tint the background of the view to selected color. Color must be from the available
     * color list in [res/values/colors]
     * @param color  the name of the color
     * @param view  the view to be have its background tinted.
     */
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

