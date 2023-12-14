package edu.uw.ischool.opensecrets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val journalFragment: JournalFragment = JournalFragment()
    private val searchFragment: SearchActivity = SearchActivity()
    private val addFragment: EntryTextActivity  = EntryTextActivity()
    private val optionFragment: OptionActivity = OptionActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating the bottom bar.
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the default fragment.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, journalFragment)
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, journalFragment)
                        .commit()
                    true
                }
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, searchFragment)
                        .commit()
                    true
                }
                R.id.add -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, addFragment)
                        .commit()
                    true
                }
                R.id.option -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, optionFragment)
                        .commit()
                    true
                }
                else -> false
            }

        }
        supportFragmentManager.setFragmentResultListener("option", this) { _, _ ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, journalFragment)
                .commit()
            binding.bottomNavigation.selectedItemId = R.id.home
        }
        supportFragmentManager.setFragmentResultListener("delete_event", this) { _, _ ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, journalFragment)
                .commit()
            binding.bottomNavigation.selectedItemId = R.id.home
        }
        supportFragmentManager.setFragmentResultListener("entry_edit", this) { _, _ ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, journalFragment)
                .commit()
            binding.bottomNavigation.selectedItemId = R.id.home
        }
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
}