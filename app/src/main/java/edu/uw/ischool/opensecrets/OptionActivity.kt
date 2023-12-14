package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import edu.uw.ischool.opensecrets.databinding.OptionBinding


class OptionActivity : AppCompatActivity() {

    private lateinit var binding: OptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.minTimeInput.setText(
            (this.application as SecretApp).optionManager.getMinTime().toString()
        )
        binding.saveOptions.setOnClickListener {
            (this.application as SecretApp).optionManager.updateMinTime(
                binding.minTimeInput.text.toString().toFloat()
            )
            Toast.makeText(this, getString(R.string.saved_option), Toast.LENGTH_SHORT).show()
            finish()
            startActivity(intent)
        }

        binding.bottomNavigation.selectedItemId = R.id.option
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
                    true
                }

                else -> false
            }
        }
    }
}