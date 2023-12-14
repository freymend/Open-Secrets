package edu.uw.ischool.opensecrets

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import edu.uw.ischool.opensecrets.adapter.EntryAdapter
import edu.uw.ischool.opensecrets.databinding.FragmentJournalBinding

class JournalFragment : Fragment() {
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)

        activity?.supportFragmentManager?.setFragmentResultListener(
            "delete_event", this
        ) { _, bundle ->

            val result = bundle.getString("delete_event").toBoolean()
            if (result) {
                parentFragmentManager.setFragmentResult("delete_event", Bundle.EMPTY)
            }
        }

        Thread {
            val username = (activity?.application as SecretApp).optionManager.getUsername()
            if (!username.isNullOrEmpty()) {
                (activity?.application as SecretApp).journalManager.restoreJournal(username)
            }

            activity?.runOnUiThread {
                // load data.
                val entries = (activity?.application as SecretApp).journalManager.loadEntry()

                // check data before loading correct view.
                if (entries.isNullOrEmpty()) {
                    binding.entryListView.visibility = View.GONE
                    binding.noEntryItem.visibility = View.VISIBLE
                } else {
                    binding.noEntryItem.visibility = View.GONE
                    binding.entryListView.visibility = View.VISIBLE
                    binding.entryListView.adapter =
                        context?.let { EntryAdapter(it, entries, ::deleteEntry) }
                }
            }
        }.start()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Start a dialog asking user if they really want to delete. Pass along the position
     * to the dialog.
     * @param pos The position of the entry that is going to be deleted
     */
    private fun deleteEntry(pos: Int) {
        val dialog =
            DeleteEntryDialogFragment((activity?.application as SecretApp).journalManager::deleteEntry)
        val args = Bundle()
        args.putInt("pos", pos)
        dialog.arguments = args
        activity?.supportFragmentManager?.let {
            dialog.show(
                it, "delete_entry"
            )
        }
    }


    /**
     * A custom Dialog Fragment that will use the callback fn to delete a journal entry
     * @param deleteFn The callback function to be call if user select "Yes" when prompted
     */
    class DeleteEntryDialogFragment(
        val deleteFn: (Int) -> Boolean
    ) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction.
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Do you want to delete this entry?")
                    .setPositiveButton("Yes") { _, _ ->
                        val pos = arguments?.getInt("pos")
                        if (pos != null) {
                            if (deleteFn(pos)) {
                                it.supportFragmentManager.setFragmentResult(
                                    "delete_event", bundleOf("delete_event" to "true")
                                )
                            } else {
                                it.supportFragmentManager.setFragmentResult(
                                    "delete_event", bundleOf("delete_event" to "false")
                                )
                            }

                        }
                    }.setNegativeButton("No") { _, _ ->
                        // User cancelled the dialog.
                    }
                // Create the AlertDialog object and return it.
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}