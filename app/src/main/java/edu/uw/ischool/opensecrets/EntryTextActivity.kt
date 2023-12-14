package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import edu.uw.ischool.opensecrets.databinding.EntryTextBinding


class EntryTextActivity : Fragment() {

    private var _binding: EntryTextBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val EDIT = "edit"
        const val INDEX = "index"
        const val TEXT = "text"
        const val TITLE = "title"
        const val COLOR = "color"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = EntryTextBinding.inflate(inflater, container, false)

        val updateCheck = activity?.intent?.extras?.getString(EDIT)
        if (updateCheck.toBoolean()) {
            binding.entry.setText(activity?.intent?.extras?.getString(TEXT).toString())
        }
        binding.overview.setOnClickListener {
            if (binding.entry.text.isNotEmpty()) {
                var bundle = bundleOf(EntryOverviewEditActivity.ENTRY to binding.entry.text.toString())
                if (updateCheck.toBoolean()) {
                    val pos = activity?.intent?.extras?.getString(INDEX).toString()
                    val titleValue = activity?.intent?.extras?.getString(TITLE).toString()
                    val colorValue = activity?.intent?.extras?.getString(COLOR).toString()
                    bundle = bundleOf(
                        EntryOverviewEditActivity.ENTRY to binding.entry.text.toString(),
                        EntryOverviewEditActivity.UPDATE to "true",
                        EntryOverviewEditActivity.POSITION to pos,
                        EntryOverviewEditActivity.TITLE to titleValue,
                        EntryOverviewEditActivity.COLOR to colorValue
                    )
                }
                parentFragmentManager.setFragmentResult("entry_create", bundle)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EntryOverviewEditActivity())
                    .commit()
            } else {
                Toast.makeText(context, "Secret should not be empty...", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}