package com.ucompensar.project_store.activities // 1. Paquete único

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView // Import necesario para el enlace de registro
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.ucompensar.project_store.R

// Asumiendo que CreateUserActivity se usa para el registro de cliente y admin.
// Si usas una específica, cámbiala a AdminCreateUserActivity
private const val ADMIN_REGISTRATION_ACTIVITY = "com.ucompensar.project_store.activities.CreateUserActivity"

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView // Referencia al enlace de registro

    // ⚠️ ADVERTENCIA: Credenciales codificadas solo para DESARROLLO/PRUEBAS.
    private val ADMIN_EMAIL = "admin@vitaverde.com"
    private val ADMIN_PASSWORD = "superadmin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        initializeView()

        loginButton.setOnClickListener {
            validateAdminLogin()
        }

        // Conexión del enlace de registro
        registerLink.setOnClickListener {
            goToAdminRegistration()
        }
    }

    // ----------------------------------------------------
    // INICIALIZACIÓN
    // ----------------------------------------------------

    private fun initializeView() {
        // Inicializa los campos de texto y el botón con sus IDs del XML
        inputEmail = findViewById(R.id.edit_admin_email)
        inputPassword = findViewById(R.id.edit_admin_password)
        loginButton = findViewById(R.id.btn_admin_login)
        registerLink = findViewById(R.id.btn_admin_to_register) // ID del TextView de registro
    }

    // ----------------------------------------------------
    // LÓGICA DE VALIDACIÓN DEL ADMINISTRADOR
    // ----------------------------------------------------

    private fun validateAdminLogin() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el correo y la contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulación de autenticación (comparación local)
        if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
            Toast.makeText(this, "Acceso concedido. ¡Bienvenido, Admin!", Toast.LENGTH_SHORT).show()
            goToAdminDashboard()
        } else {
            Toast.makeText(this, "Credenciales incorrectas o usuario no es administrador.", Toast.LENGTH_LONG).show()
        }
    }

    // ----------------------------------------------------
    // NAVEGACIÓN
    // ----------------------------------------------------

    private fun goToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        startActivity(intent)
        finish() // Cierra la Activity de login
    }

    private fun goToAdminRegistration() {
        // Redirige al flujo de creación de cuenta (Admin Sign Up)
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
        // No finalizamos esta activity para permitir que el admin regrese si cancela el registro
    }
}