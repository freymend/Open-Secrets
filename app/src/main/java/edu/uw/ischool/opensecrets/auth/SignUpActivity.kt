package edu.uw.ischool.opensecrets.auth

import android.content.Intent
import android.os.Bundle
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

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUp = findViewById<Button>(R.id.sign_up)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val verifyPassword = findViewById<EditText>(R.id.verify_password)

        username.addTextChangedListener {
            signUp.isEnabled =
                username.text.isNotEmpty() && password.text.isNotEmpty() && verifyPassword.text.isNotEmpty()
        }
        password.addTextChangedListener {
            signUp.isEnabled =
                username.text.isNotEmpty() && password.text.isNotEmpty() && verifyPassword.text.isNotEmpty()
        }
        verifyPassword.addTextChangedListener {
            signUp.isEnabled =
                username.text.isNotEmpty() && password.text.isNotEmpty() && verifyPassword.text.isNotEmpty()
        }

        signUp.setOnClickListener {
            if (password.text.toString() != verifyPassword.text.toString()) {
                Toast.makeText(this, getString(R.string.check_password), Toast.LENGTH_SHORT).show()
            }
            Thread {
                val response =
                    with(URL("https://not-open-secrets.fly.dev/register").openConnection() as HttpURLConnection) {
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
                    if (response.getBoolean("registered")) {
//                        TODO: put home screen here
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, getString(R.string.username_taken), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.start()
        }
    }
}