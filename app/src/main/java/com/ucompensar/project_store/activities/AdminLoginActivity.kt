package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.ucompensar.project_store.R



class AdminLoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var loginButton: Button


    private val ADMIN_EMAIL = "admin@vitaverde.com"
    private val ADMIN_PASSWORD = "superadmin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        initializeView()

        loginButton.setOnClickListener {
            validateAdminLogin()
        }


    }



    private fun initializeView() {

        inputEmail = findViewById(R.id.edit_admin_email)
        inputPassword = findViewById(R.id.edit_admin_password)
        loginButton = findViewById(R.id.btn_admin_login)

    }



    private fun validateAdminLogin() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el correo y la contraseña.", Toast.LENGTH_SHORT).show()
            return
        }


        if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
            Toast.makeText(this, "Acceso concedido. ¡Bienvenido, Admin!", Toast.LENGTH_SHORT).show()
            goToAdminDashboard()
        } else {
            Toast.makeText(this, "Credenciales incorrectas o usuario no es administrador.", Toast.LENGTH_LONG).show()
        }
    }



    private fun goToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        startActivity(intent)
        finish() // Cierra la Activity de login
    }
}