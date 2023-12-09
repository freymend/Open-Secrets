package edu.uw.ischool.opensecrets.util

import android.content.Context
import android.content.SharedPreferences
import edu.uw.ischool.opensecrets.R

class OptionManager(val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        context.resources.getString(R.string.preference_key),
        Context.MODE_PRIVATE
    )

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun getPassword(): String? {
        return prefs.getString(PASSWORD, null)
    }

    fun updatePassword(password: String) {
        prefs.edit().putString(PASSWORD, password).apply()
    }

    fun updateUsername(username: String) {
        prefs.edit().putString(USERNAME, username).apply()
    }
}