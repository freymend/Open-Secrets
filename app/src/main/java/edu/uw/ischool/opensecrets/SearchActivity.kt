package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.databinding.SearchBinding


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: SearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    EntryTextActivity::class.java
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
        binding.optionButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    OptionActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
    }
}