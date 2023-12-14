package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.auth.LoginActivity
import edu.uw.ischool.opensecrets.auth.SignUpActivity


class MainActivity : AppCompatActivity() {
    private val loginFragment = LoginActivity()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()
        (this.application as SecretApp).loadRepository()

        if ((this.application as SecretApp).optionManager.getUsername() == null || (this.application as SecretApp).optionManager.getPassword() == null) {
            supportFragmentManager.beginTransaction().add(android.R.id.content, loginFragment)
                .commit()
            supportFragmentManager.setFragmentResultListener("login", this) { _, _ ->
                startActivity(Intent(this, HomeActivity::class.java))
            }
            supportFragmentManager.setFragmentResultListener("signup", this) { _, _ ->
                supportFragmentManager.beginTransaction().remove(loginFragment)
                    .add(android.R.id.content, SignUpActivity()).commit()
            }
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.READ_PHONE_STATE,
            ), 0
        )
    }
}