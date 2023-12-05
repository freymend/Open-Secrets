package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.auth.LoginActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", filesDir.toString())

        val journal = File(filesDir, "journal.json")
        if (!journal.exists()) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
//            TODO: put home screen here
        }
    }
}