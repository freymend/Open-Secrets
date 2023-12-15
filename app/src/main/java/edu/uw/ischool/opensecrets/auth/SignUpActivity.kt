package edu.uw.ischool.opensecrets.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import edu.uw.ischool.opensecrets.R
import edu.uw.ischool.opensecrets.util.Request

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUp = findViewById<Button>(R.id.sign_up)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val verifyPassword = findViewById<EditText>(R.id.verify_password)

        val isFilled =
            { username.text.isNotEmpty() && password.text.isNotEmpty() && verifyPassword.text.isNotEmpty() }

        username.addTextChangedListener {
            signUp.isEnabled = isFilled()
        }
        password.addTextChangedListener {
            signUp.isEnabled = isFilled()
        }
        verifyPassword.addTextChangedListener {
            signUp.isEnabled = isFilled()
        }

        val passwordIsVerified = { password.text.toString() == verifyPassword.text.toString() }

        signUp.setOnClickListener {
            if (!passwordIsVerified()) {
                Toast.makeText(this, getString(R.string.check_password), Toast.LENGTH_SHORT).show()
            }
            Thread {
                val response = Request.post(
                    "https://not-open-secrets.fly.dev/register", """{
                        "username": "${username.text}",
                        "password": "${password.text}"
                    }""".trimIndent()
                )

                runOnUiThread {
                    if (response.getBoolean("registered")) {
                        startActivity(
                            Intent(
                                    this, LoginActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                    } else {
                        Toast.makeText(this, getString(R.string.username_taken), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.start()
        }
    }
}