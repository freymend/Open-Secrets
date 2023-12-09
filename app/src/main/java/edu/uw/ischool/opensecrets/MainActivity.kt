package edu.uw.ischool.opensecrets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.uw.ischool.opensecrets.auth.LoginActivity
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import edu.uw.ischool.opensecrets.adapter.EntryAdapter
import edu.uw.ischool.opensecrets.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", filesDir.toString())

        if ((this.application as SecretApp).optionManager.getUsername() == null ||
            (this.application as SecretApp).optionManager.getPassword() == null
        ) {
            if (!(this.application as SecretApp).journalManager.journalExist()) {
                Log.d("fileCreate", (this.application as SecretApp).journalManager.createJournal().toString())
            }
            startActivity(Intent(this, LoginActivity::class.java))
        } else {

            /**
             * Listener for the delete dialog, and restart the activity if the result is true
             * Some interesting note:
             * this.recreate() crashes the activity, while finish() and startActivity(intent)
             * Probably b/c the fragment is not yet closed when the listener is call, thus
             * when calling recreate(), the application tried to remake the activity and fragment.
             * But, finish() just close both activity and fragment, and startActivity is forced to
             * start a new activity. Just a guess, tho.
             */
            this.supportFragmentManager.setFragmentResultListener(
                "delete_event",
                this
            ) { _, bundle ->

                val result = bundle.getString("delete_event").toBoolean()
                if (result) {
                    finish()
                    startActivity(intent)
                }
            }

            // Creating the bottom bar.
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.searchButton.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        SearchActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                )
            }
            binding.addButton.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        EntryTextActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                )
            }
            binding.optionButton.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        OptionActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                )
            }

            // load data.
            val entries = (this.application as SecretApp).journalManager.loadEntry()

            // check data before loading correct view.
            if (entries.isNullOrEmpty()) {
                binding.entryListView.visibility = View.GONE
                binding.noEntryItem.visibility = View.VISIBLE
            } else {
                binding.noEntryItem.visibility = View.GONE
                binding.entryListView.visibility = View.VISIBLE
                binding.entryListView.adapter = EntryAdapter(this, entries, ::deleteEntry)
            }
        }
    }

    /**
     * Start a dialog asking user if they really want to delete. Pass along the position
     * to the dialog.
     * @param pos The position of the entry that is going to be deleted
     */
    private fun deleteEntry(pos: Int) {
        val dialog =
            DeleteEntryDialogFragment((this.application as SecretApp).journalManager::deleteEntry)
        val args = Bundle()
        args.putInt("pos", pos)
        dialog.arguments = args
        dialog.show(
            this.supportFragmentManager,
            "delete_entry"
        )
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
                                    "delete_event",
                                    bundleOf("delete_event" to "true")
                                )
                            } else {
                                it.supportFragmentManager.setFragmentResult(
                                    "delete_event",
                                    bundleOf("delete_event" to "false")
                                )
                            }

                        }
                    }
                    .setNegativeButton("No") { _, _ ->
                        // User cancelled the dialog.
                    }
                // Create the AlertDialog object and return it.
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}