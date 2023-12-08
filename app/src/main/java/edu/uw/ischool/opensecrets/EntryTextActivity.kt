package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.databinding.EntryTextBinding


class EntryTextActivity : AppCompatActivity() {

    private lateinit var binding: EntryTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntryTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.overview.setOnClickListener {
            if (binding.entry.text.isNotEmpty()) {
                val intent = Intent(
                    this,
                    EntryOverviewEditActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra(EntryOverviewEditActivity.ENTRY, binding.entry.text.toString())
                startActivity(
                    intent
                )
            } else {
                Toast.makeText(this, "Secret should not be empty...", Toast.LENGTH_SHORT).show()
            }
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
        binding.homeButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
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
    }
}