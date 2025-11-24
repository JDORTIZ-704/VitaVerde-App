package com.ucompensar.project_store.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_session", Context.MODE_PRIVATE)

    // Claves de la sesión
    private val KEY_NAME = "user_name"
    private val KEY_LOGGED_IN = "is_logged_in"
    private val KEY_USER_ID = "user_id"
    private val KEY_EMAIL = "email"
    private val KEY_PROVIDER = "provider"


    /**
     * Guarda los datos de la sesión del usuario, incluyendo el nombre.
     */
    fun saveSession(userId: Int, name: String, email: String, provider: String) {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_NAME, name) // ⭐️ Guardar el nombre
            putString(KEY_EMAIL, email)
            putString(KEY_PROVIDER, provider)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    /**
     * Método para obtener el nombre del usuario.
     */
    fun getName(): String? = prefs.getString(KEY_NAME, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getProvider(): String? = prefs.getString(KEY_PROVIDER, null)

    /**
     * Cierra la sesión eliminando todas las credenciales guardadas.
     */
    fun clear() {
        prefs.edit {clear()}
    }
}