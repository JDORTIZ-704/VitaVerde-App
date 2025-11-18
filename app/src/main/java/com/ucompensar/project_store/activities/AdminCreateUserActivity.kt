package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.UsersDAO
import com.ucompensar.project_store.models.Users
import android.widget.TextView // ⭐️ CORRECCIÓN 1: Importar TextView ⭐️

// Se asume que AdminDashboardActivity y LoginActivity existen.
class AdminCreateUserActivity : AppCompatActivity() {

    // Declarar las vistas
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etCity: EditText
    private lateinit var etRole: EditText
    private lateinit var btnCreateUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_user)

        // 1. Inicializar vistas con los IDs del XML
        etCity = findViewById(R.id.input_admin_register_city)
        etName = findViewById(R.id.input_admin_register_username)
        etRole = findViewById(R.id.input_admin_register_role)
        etEmail = findViewById(R.id.input_admin_register_email)
        etPassword = findViewById(R.id.input_admin_register_password)
        btnCreateUser = findViewById(R.id.btn_admin_create_user_register)

        // 2. Configurar Listener para el botón de creación de usuario
        btnCreateUser.setOnClickListener {
            handleAdminRegistration()
        }

        // 3. ⭐️ CORRECCIÓN 2 y 3: Listener para volver a Login ⭐️
        // Se usa findViewById<TextView> para asegurar el tipo.
        // Se corrige 'setOnclickListener' a 'setOnClickListener'.
        findViewById<TextView>(R.id.btn_admin_register_to_login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleAdminRegistration() {
        // 1. Obtener los valores de los campos de entrada
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val city = etCity.text.toString().trim()
        val role = etRole.text.toString().trim()

        // 2. Validación básica de campos obligatorios
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || city.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val usersDAO = UsersDAO(this)

        // 3. Verificar si el correo ya existe
        if (usersDAO.validateEmail(email)) {
            Toast.makeText(this, "Error: El correo '$email' ya está registrado.", Toast.LENGTH_LONG).show()
            return
        }

        // 4. Crear el objeto Users
        val newUser = Users(
            name = name,
            email = email,
            password = password,
            isAdmin = true,
            city = city,
            role = role
        )

        // 5. Registrar el usuario
        if (usersDAO.registerUser(newUser)) {
            Toast.makeText(this, "Administrador '$name' creado exitosamente!", Toast.LENGTH_LONG).show()

            // Redirigir al Dashboard del Administrador
            // ⭐️ CORRECCIÓN 4: AdminDashboardActivity debe existir en el paquete. ⭐️
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Error: Falló la inserción del nuevo administrador.", Toast.LENGTH_LONG).show()
        }
    }
}