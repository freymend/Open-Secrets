package edu.uw.ischool.opensecrets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import edu.uw.ischool.opensecrets.adapter.EntryAdapter
import edu.uw.ischool.opensecrets.databinding.ActivityHomeBinding
import edu.uw.ischool.opensecrets.model.Entry


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var allEntries : MutableList<Entry> = mutableListOf()
    private var sortedEntries : MutableList<Entry> = mutableListOf()
    private var entryToPos : MutableMap<Entry, Int> = mutableMapOf()
    private var adapter : ArrayAdapter<Entry>? = null
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
        binding.searchButton.setOnClickListener {
            startActivity(
                Intent(
                    this, SearchActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        binding.addButton.setOnClickListener {
            startActivity(
                Intent(
                    this, EntryTextActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        binding.optionButton.setOnClickListener {
            startActivity(
                Intent(
                    this, OptionActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        Thread {
            val username = (this.application as SecretApp).optionManager.getUsername()
            if (!username.isNullOrEmpty()) {
                (this.application as SecretApp).journalManager.restoreJournal(username)
            }

            runOnUiThread {
                // load data.
                val nullEntries = (this.application as SecretApp).journalManager.loadEntry()
                val entries : List<Entry> = nullEntries ?: mutableListOf()

                Log.i("HomeActivity", "running on UI Thread")
                // check data before loading correct view.
                if (entries.isNullOrEmpty()) {
                    binding.entryListView.visibility = View.GONE
                    binding.noEntryItem.visibility = View.VISIBLE
                    binding.clearSearchBtn.visibility = View.GONE
                } else {
                    allEntries.addAll(entries)
                    for(i : Int in 0..<allEntries.size){
                        entryToPos[allEntries[i]] = i
                    }

                    val intentExtras : Bundle? = intent.extras
                    if(intentExtras != null){
                        val sortByString : String = intentExtras.getString(SORTBY, "dateCreated")
                        val ascending : Boolean =  intentExtras.getBoolean(ASCEND, true)
                        val filterText : String = intentExtras.getString(FILTERTEXT, "")
                        val filterBy : String = intentExtras.getString(FILTERBY, "title")

                        val filtered : List<Entry> = applyFilter(entries, filterText, filterBy)
                        val sorted : List<Entry> = sortEntries(filtered, sortByString, ascending)
                        sortedEntries.addAll(sorted)
                        binding.clearSearchBtn.visibility = View.VISIBLE
                        binding.clearSearchBtn.setOnClickListener{
                            sortedEntries.clear()
                            sortedEntries.addAll(allEntries)
                            adapter?.notifyDataSetChanged()
                            it.visibility = View.GONE
                        }
                    }
                    else{
                        Log.i("HomeActivity", "intentExtras Failed")
                        sortedEntries.addAll(entries)
                        binding.clearSearchBtn.visibility = View.GONE
                    }

                    adapter = EntryAdapter(this, sortedEntries, ::deleteEntry)
                    binding.noEntryItem.visibility = View.GONE
                    binding.entryListView.visibility = View.VISIBLE
                    binding.entryListView.adapter = adapter
//                    sortEntriesByTitle()
                }

            }
        }.start()
    }
    fun applyFilter(entries : List<Entry>, filterText : String, filterBy : String) : List<Entry> {
        if(filterText.isNotEmpty()){
            return when(filterBy) {
                "Title" -> entries.filter { it.title.contains(filterText) }
                "Text" ->  entries.filter { it.text.contains(filterText) }
                "Color" ->  entries.filter { it.color.contains(filterText) }
                else -> entries
            }
        }
        return entries
    }
    fun sortEntries(entries : List<Entry>, sortByString : String, ascending : Boolean) : List<Entry> {
        return when(sortByString) {
            "Title" -> if(ascending) entries.sortedBy { it.title } else entries.sortedByDescending { it.title }
            "dateCreated" -> if(ascending) entries.sortedBy { it.dateCreated } else entries.sortedByDescending { it.dateCreated }
            else -> entries
        }
    }
    fun sortEntriesByTitle(){
        sortedEntries.clear()
        val results : List<Entry> = allEntries.sortedWith(compareBy { it.title })
        sortedEntries.addAll(results)
        adapter?.notifyDataSetChanged()
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
            DeleteEntryDialogFragment((this.application as SecretApp).journalManager::deleteEntry,
                (this.application as SecretApp)::queueEntryFromPos)
        val args = Bundle()
        val realPos : Int = adapterPosToEntryPos(pos)
        args.putInt("pos", realPos)
        dialog.arguments = args
        dialog.show(
            this.supportFragmentManager, "delete_entry"
        )
    }

    private fun adapterPosToEntryPos(pos : Int) : Int {
        val entry : Entry = sortedEntries[pos]
        val realPos : Int = entryToPos[entry] ?: -1
        Log.i("DeleteAction", "input: $entry\noutput: ${allEntries[realPos]}")
        return realPos
    }

    /**
     * A custom Dialog Fragment that will use the callback fn to delete a journal entry
     * @param deleteFn The callback function to be call if user select "Yes" when prompted
     */
    class DeleteEntryDialogFragment(
        val deleteFn: (Int) -> Boolean, val sendEntry: (Int) -> Unit
    ) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction.
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Do you want to delete this entry?")
                    .setPositiveButton("Yes") { _, _ ->
                        val pos = arguments?.getInt("pos")
                        if (pos != null) {
                            sendEntry(pos)
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