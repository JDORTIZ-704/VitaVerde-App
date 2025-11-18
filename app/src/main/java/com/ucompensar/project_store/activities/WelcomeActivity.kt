package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView // <-- CORRECCIÓN: Importa la clase TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        // Initialize the button (Client Login)
        val btnEnterWelcome: Button = findViewById(R.id.btn_enter_welcome)

        // CORRECCIÓN: Inicialización del TextView (Admin Login)
        val textAdminLoginLink: TextView = findViewById(R.id.text_admin_login_link)

        // ----------------------------------------------------
        // CLIENTE (Botón principal)
        // ----------------------------------------------------
        btnEnterWelcome.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // ----------------------------------------------------
        // ADMINISTRADOR (Enlace de texto)
        // ----------------------------------------------------
        textAdminLoginLink.setOnClickListener {
            // Asegúrate de que AdminLoginActivity exista
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
            // No usamos finish() para permitir al usuario volver a la bienvenida.
        }
    }
}