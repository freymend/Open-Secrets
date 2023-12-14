package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import edu.uw.ischool.opensecrets.databinding.EntryOverviewBinding
import edu.uw.ischool.opensecrets.model.Entry
import java.util.Calendar


class EntryOverviewEditActivity : AppCompatActivity() {

    private lateinit var binding: EntryOverviewBinding
    private lateinit var secretApp : SecretApp

    companion object {
        const val ENTRY = "entry"
        const val UPDATE = "update"
        const val POSITION = "position"
        const val TITLE = "title"
        const val COLOR = "color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secretApp = (this.application as SecretApp)
        binding = EntryOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val colors = resources.getStringArray(R.array.color_array)
        binding.optionButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    OptionActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        binding.homeButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        binding.searchButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.color_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.colorSpinner.adapter = adapter
        }

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

        val updateCheck = this.intent?.extras?.getString(UPDATE).toBoolean()
        if (updateCheck) {
            // setUpdateListener
            // hide save button
            // show edit button
            binding.entryEditButton.visibility = View.VISIBLE
            binding.entrySaveButton.visibility = View.GONE
            val colorExtra = intent?.extras?.getString(COLOR).toString()
            binding.colorSpinner.setSelection(colors.asList().indexOf(colorExtra))
            val titleExtra = intent?.extras?.getString(TITLE).toString()
            binding.entryTitle.setText(titleExtra)
            binding.entryEditButton.setOnClickListener {
                if (binding.entryTitle.text.isEmpty()) {
                    Toast.makeText(this, "Title should not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val pos = intent?.extras?.getString(POSITION).toString().toInt()
                    secretApp.queueEntryFromPos(pos)

                    val status = secretApp.journalManager.updateEntry(
                        pos,
                        Entry(
                            binding.entryTitle.text.toString(),
                            binding.colorSpinner.selectedItem as String,
                            intent?.extras?.getString(ENTRY).toString(),
                            Calendar.getInstance().time
                        )
                    )

                    if (status) {
                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        )
                    } else {
                        Toast.makeText(this, "Failed to write data", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            binding.entryEditButton.visibility = View.GONE
            binding.entrySaveButton.visibility = View.VISIBLE
            binding.entrySaveButton.setOnClickListener {
                if (binding.entryTitle.text.isEmpty()) {
                    Toast.makeText(this, "Title should not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val entry : Entry = Entry(
                        binding.entryTitle.text.toString(),
                        binding.colorSpinner.selectedItem as String,
                        intent?.extras?.getString(ENTRY).toString(),
                        Calendar.getInstance().time
                    )
                    secretApp.queueSendEntry(entry)
                    val status = secretApp.journalManager.appendEntry(
                        entry
                    )

                    if (status) {
                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        )
                    } else {
                        Toast.makeText(this, "Failed to write data", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}