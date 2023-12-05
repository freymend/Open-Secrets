package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.databinding.EntryOverviewBinding


class EntryOverviewEditActivity : AppCompatActivity() {

    private lateinit var binding: EntryOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntryOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.optionButton.setOnClickListener {
            startActivity(Intent(this, OptionActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            overridePendingTransition(0, 0)
        }
        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }
}