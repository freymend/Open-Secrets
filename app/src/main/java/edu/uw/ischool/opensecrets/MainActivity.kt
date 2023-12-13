package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.auth.LoginActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()
        (this.application as SecretApp).loadRepository()

        if ((this.application as SecretApp).optionManager.getUsername() == null || (this.application as SecretApp).optionManager.getPassword() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
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