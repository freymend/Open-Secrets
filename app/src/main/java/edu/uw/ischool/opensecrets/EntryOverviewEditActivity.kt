package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import edu.uw.ischool.opensecrets.databinding.EntryOverviewBinding
import edu.uw.ischool.opensecrets.model.Entry
import java.util.Calendar


class EntryOverviewEditActivity : Fragment() {

    private var _binding: EntryOverviewBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val ENTRY = "entry"
        const val UPDATE = "update"
        const val POSITION = "position"
        const val TITLE = "title"
        const val COLOR = "color"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EntryOverviewBinding.inflate(inflater, container, false)
        val colors = resources.getStringArray(R.array.color_array)

        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (colors[position]) {
                    "red" -> binding.colorPreview.background =
                        ResourcesCompat.getDrawable(resources, R.color.red, null)

                    "blue" -> binding.colorPreview.background =
                        ResourcesCompat.getDrawable(resources, R.color.blue, null)

                    "green" -> binding.colorPreview.background =
                        ResourcesCompat.getDrawable(resources, R.color.green, null)

                    "purple" -> binding.colorPreview.background =
                        ResourcesCompat.getDrawable(resources, R.color.purple, null)

                    "yellow" -> binding.colorPreview.background =
                        ResourcesCompat.getDrawable(resources, R.color.yellow, null)

                    "orange" -> binding.colorPreview.background =
                        ResourcesCompat.getDrawable(resources, R.color.orange, null)

                    else -> {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val updateCheck = activity?.intent?.extras?.getString(UPDATE).toBoolean()
        if (updateCheck) {
            // setUpdateListener
            // hide save button
            // show edit button
            binding.entryEditButton.visibility = View.VISIBLE
            binding.entrySaveButton.visibility = View.GONE
            val colorExtra = activity?.intent?.extras?.getString(COLOR).toString()
            binding.colorSpinner.setSelection(colors.asList().indexOf(colorExtra))
            val titleExtra = activity?.intent?.extras?.getString(TITLE).toString()
            binding.entryTitle.setText(titleExtra)
            binding.entryEditButton.setOnClickListener {
                if (binding.entryTitle.text.isEmpty()) {
                    Toast.makeText(context, "Title should not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val pos = activity?.intent?.extras?.getString(POSITION).toString().toInt()
                    val status = (activity?.application as SecretApp).journalManager.updateEntry(
                        pos,
                        Entry(
                            binding.entryTitle.text.toString(),
                            binding.colorSpinner.selectedItem as String,
                            activity?.intent?.extras?.getString(ENTRY).toString(),
                            Calendar.getInstance().time
                        )
                    )

                    if (status) {
                        parentFragmentManager.setFragmentResult("entry_edit", Bundle.EMPTY)
                    } else {
                        Toast.makeText(context, "Failed to write data", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            binding.entryEditButton.visibility = View.GONE
            binding.entrySaveButton.visibility = View.VISIBLE
            binding.entrySaveButton.setOnClickListener {
                if (binding.entryTitle.text.isEmpty()) {
                    Toast.makeText(context, "Title should not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val status = (activity?.application as SecretApp).journalManager.appendEntry(
                        Entry(
                            binding.entryTitle.text.toString(),
                            binding.colorSpinner.selectedItem as String,
                            activity?.intent?.extras?.getString(ENTRY).toString(),
                            Calendar.getInstance().time
                        )
                    )

                    if (status) {
                        parentFragmentManager.setFragmentResult("entry_edit", Bundle.EMPTY)
                    } else {
                        Toast.makeText(context, "Failed to write data", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}