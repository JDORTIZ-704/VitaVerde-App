package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.textfield.TextInputEditText
// import com.ucompensar.project_store.MainActivity // No se usa directamente aquí, pero puede ser útil
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.UsersDAO
import com.ucompensar.project_store.models.Users
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var input_mail_login: TextInputEditText
    private lateinit var input_password_login: TextInputEditText
    private val usersDAO by lazy { UsersDAO(this) }
    // Asumiendo que SessionManager existe en el paquete principal
    private val session by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Si ya hay sesión, salta a Main
        if (session.isLoggedIn()) {
            goToMain()
            return
        }

        initializeView()
    }

    private fun initializeView() {

        // Inputs
        input_mail_login = findViewById(R.id.input_mail_login)
        input_password_login = findViewById(R.id.input_password_login)

        // Buttons
        val createUserButton: TextView = findViewById(R.id.btn_login_to_register)
        val googleButton: Button = findViewById(R.id.btn_google_get_into_user)
        val loginButton: Button = findViewById(R.id.btn_create_user_login)

        // 1. ADICIÓN CLAVE: Enlace a Login de Administrador
        val adminLoginButton: TextView = findViewById(R.id.btn_login_to_admin)

        createUserButton.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        googleButton.setOnClickListener {
            loginWithGoogle()
            temporaryMessageEnterGoogle()
        }

        loginButton.setOnClickListener {
            validateLogin()
        }

        // Conexión del enlace a Admin
        adminLoginButton.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
            // No se usa finish() para permitir al usuario volver al login de cliente
        }
    }

    private fun validateLogin() {
        val mail = input_mail_login.text.toString().trim()
        val password = input_password_login.text.toString().trim()
        val user = usersDAO.getUserByEmail(mail)

        // 2. CORRECCIÓN: Mensajes a Español
        if (mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            mail.isEmpty() -> {
                input_mail_login.error = "Por favor, ingresa un correo"
                input_mail_login.requestFocus()
                return
            }
            password.isEmpty() -> {
                input_password_login.error = "Por favor, ingresa una contraseña"
                input_password_login.requestFocus()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                input_mail_login.error = "Por favor, ingresa un correo válido"
                input_mail_login.requestFocus()
                return
            }
            else -> {
                // Limpieza de errores
                input_mail_login.error = null
                input_password_login.error = null

                // Inicio de sesión
                if (user != null && usersDAO.validateLogin(mail, password)) {
                    session.saveSession(user.id!!, user.email, "local")
                    temporaryMessageEnter()
                    goToMain()
                } else {
                    Toast.makeText(this, "Fallo al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // -------- Login con Google (sin cambios)
    private fun loginWithGoogle() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // ... (Lógica de Credential Manager con Google, sin cambios) ...
                val credentialManager = CredentialManager.create(this@LoginActivity)
                val googleOption = GetSignInWithGoogleOption
                    .Builder("1030399245156-taae6cddrnpd2ft7e1ps6c8obctqg7k9.apps.googleusercontent.com")
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                val result = credentialManager.getCredential(this@LoginActivity, request)
                val cred = result.credential
                if (cred is CustomCredential &&
                    cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val google = GoogleIdTokenCredential.createFrom(cred.data)
                    val email = google.id
                    val name = google.displayName ?: "Usuario"
                    val subLike = google.idToken

                    usersDAO.registerGoogleUser(
                        Users(
                            name = name,
                            email = email,
                            password = null,
                            provider = "google",
                            providerUserId = subLike
                        )
                    )

                    val user = usersDAO.getUserByEmail(email)
                    if (user?.id != null) {
                        session.saveSession(user.id, user.email, "google")
                        Toast.makeText(this@LoginActivity, "Inicio con Google exitoso", Toast.LENGTH_SHORT).show()
                        goToMain()
                    } else {
                        Toast.makeText(this@LoginActivity, "No se pudo crear la sesión", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error al iniciar con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMain() {
        // Redirige a SetLocationActivity (confirmado en el flujo)
        startActivity(Intent(this, SetLocationActivity::class.java))
        finish()
    }



    // Temporary messages (Corregidos a español)

    private fun temporaryMessageEnterGoogle () {
        val toast = Toast.makeText(this, "Login con Google", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }

    private fun temporaryMessageEnter () {
        val toast = Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }

}