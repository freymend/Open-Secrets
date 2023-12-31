package edu.uw.ischool.opensecrets.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import edu.uw.ischool.opensecrets.HomeActivity
import edu.uw.ischool.opensecrets.MainActivity
import edu.uw.ischool.opensecrets.R
import edu.uw.ischool.opensecrets.SecretApp
import edu.uw.ischool.opensecrets.util.Request

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login = findViewById<Button>(R.id.login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        val isFilled = { username.text.isNotEmpty() && password.text.isNotEmpty() }

        username.addTextChangedListener {
            login.isEnabled = isFilled()
        }
        password.addTextChangedListener {
            login.isEnabled = isFilled()
        }

        findViewById<Button>(R.id.sign_up).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        login.setOnClickListener {
            Thread {
                val response = Request.post(
                    "https://not-open-secrets.fly.dev/login", """{
                        "username": "${username.text}",
                        "password": "${password.text}"
                    }""".trimIndent()
                )

                runOnUiThread {
                    if (response.getBoolean("authenticated")) {
                        (this.application as SecretApp).optionManager.updateUsername(username.text.toString())
                        (this.application as SecretApp).optionManager.updatePassword(password.text.toString())
                        startActivity(
                            Intent(
                                this, HomeActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.start()
        }
    }
}