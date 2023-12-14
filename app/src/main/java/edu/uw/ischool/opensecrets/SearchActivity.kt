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

        binding.bottomNavigation.selectedItemId = R.id.search
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    startActivity(
                        Intent(
                            this, HomeActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    true
                }

                R.id.search -> {
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
    }
}