package com.ucompensar.project_store.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_session", Context.MODE_PRIVATE)

    fun saveSession(userId: Int, email: String, provider: String) {
        prefs.edit {
            putBoolean("is_logged_in", true)
            putInt("user_id", userId)
            putString("email", email)
            putString("provider", provider)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun getEmail(): String? = prefs.getString("email", null)

    fun getProvider(): String? = prefs.getString("provider", null)

    fun clear() {
        prefs.edit {clear()}
    }
}