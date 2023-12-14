package edu.uw.ischool.opensecrets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import edu.uw.ischool.opensecrets.adapter.EntryAdapter
import edu.uw.ischool.opensecrets.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportFragmentManager.setFragmentResultListener(
            "delete_event", this
        ) { _, bundle ->

            val result = bundle.getString("delete_event").toBoolean()
            if (result) {
                finish()
                startActivity(intent)
            }
        }

        // Creating the bottom bar.
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.home
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    true
                }

                R.id.search -> {
                    startActivity(
                        Intent(
                            this, SearchActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    true
                }

                R.id.add -> {
                    startActivity(
                        Intent(
                            this, EntryTextActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    true
                }

                R.id.option -> {
                    startActivity(
                        Intent(
                            this, OptionActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    true
                }

                else -> false
            }
        }
        Thread {
            val username = (this.application as SecretApp).optionManager.getUsername()
            if (!username.isNullOrEmpty()) {
                (this.application as SecretApp).journalManager.restoreJournal(username)
            }

            runOnUiThread {
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
        }.start()
    }

    override fun onPause() {
        super.onPause()
        val username = (this.application as SecretApp).optionManager.getUsername()
        if (!username.isNullOrEmpty()) {
            Thread {
                (this.application as SecretApp).journalManager.backupJournal(username)
            }.start()
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
            this.supportFragmentManager, "delete_entry"
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