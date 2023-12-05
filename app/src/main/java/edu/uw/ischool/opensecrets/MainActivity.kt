package edu.uw.ischool.opensecrets

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.uw.ischool.opensecrets.auth.LoginActivity
import java.io.File
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import edu.uw.ischool.opensecrets.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", filesDir.toString())

        val journal = File(filesDir, "journal.json")
        if (!journal.exists()) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
//            TODO: put home screen here

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.searchButton.setOnClickListener {
                startActivity(Intent(this, SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            }
            binding.addButton.setOnClickListener {
                startActivity(Intent(this, EntryTextActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            }
            binding.optionButton.setOnClickListener {
                startActivity(Intent(this, OptionActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            }
        }
    }
}