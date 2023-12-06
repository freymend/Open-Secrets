package edu.uw.ischool.opensecrets.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import edu.uw.ischool.opensecrets.MainActivity
import edu.uw.ischool.opensecrets.R
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login = findViewById<Button>(R.id.login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        username.addTextChangedListener {
            login.isEnabled = username.text.isNotEmpty() && password.text.isNotEmpty()
        }
        password.addTextChangedListener {
            login.isEnabled = username.text.isNotEmpty() && password.text.isNotEmpty()
        }

        findViewById<Button>(R.id.sign_up).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        login.setOnClickListener {
            Thread {
                val response =
                    with(URL("https://not-open-secrets.fly.dev/login").openConnection() as HttpURLConnection) {
                        requestMethod = "POST"

                        connectTimeout = 5000

                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("Accept", "application/json")

                        doOutput = true
                        setChunkedStreamingMode(0)
                        BufferedOutputStream(outputStream).use {
                            it.write(
                                """{
                                    "username": "${username.text}",
                                    "password": "${password.text}"
                                }""".trimIndent().toByteArray()
                            )
                            it.flush()
                        }

                        BufferedReader(InputStreamReader(inputStream)).use {
                            JSONObject(it.readText())
                        }
                    }

                runOnUiThread {
                    if (response.getBoolean("authenticated")) {
//                        TODO: put home screen here
                        val prefs = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
                        val prefsEditor = prefs.edit()
                        prefsEditor.putString("username", username.text.toString())
                        prefsEditor.putString("password", password.text.toString())
                        prefsEditor.apply()
                        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }
}