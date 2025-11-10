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
import com.google.android.material.textfield.TextInputEditText
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.UsersDAO
import com.ucompensar.project_store.models.Users

class CreateUserActivity : AppCompatActivity() {

    private lateinit var input_user_register: TextInputEditText
    private lateinit var input_email_register: TextInputEditText
    private lateinit var input_password_register: TextInputEditText
    private lateinit var usersDAO: UsersDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_user)

        // Initialize DAO

        usersDAO = UsersDAO(this)

        // Initialize views

        initializeViews()
    }

    private fun initializeViews() {

        // Inputs

        input_user_register = findViewById(R.id.input_user_register)
        input_email_register = findViewById(R.id.input_email_register)
        input_password_register = findViewById(R.id.input_password_register)

        // Buttons

        val createAccountButton: Button = findViewById(R.id.btn_create_user_register)
        createAccountButton.setOnClickListener {
            registerUser()
        }

        val googleRegistrationButton: Button = findViewById(R.id.btn_google_create_user)
        googleRegistrationButton.setOnClickListener {
            temporaryMessageGoogleLogin()
        }

        val loginBackButton: TextView = findViewById(R.id.btn_register_to_login)
        loginBackButton.setOnClickListener {
        val intentLoginBackButton = Intent(this, LoginActivity::class.java)
        startActivity(intentLoginBackButton)
        finish()
        }
    }

    // User registration validation

    private fun registerUser() {

        // Get values from the fields

        val username = input_user_register.text.toString().trim()
        val email = input_email_register.text.toString().trim()
        val password = input_password_register.text.toString().trim()

        // Validate fields

        when {
            username.isEmpty() -> {
                input_user_register.error = "Username is required"
                input_user_register.requestFocus()
                return
            }
            email.isEmpty() -> {
                input_email_register.error = "Email is required"
                input_email_register.requestFocus()
                return
            }
            password.isEmpty() -> {
                input_password_register.error = "Password is required"
                input_password_register.requestFocus()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                input_email_register.error = "Invalid email"
                input_email_register.requestFocus()
                return
            }
            password.length < 6 -> {
                input_password_register.error = "Password must be at least 6 characters"
                input_password_register.requestFocus()
                return
            }
            usersDAO.validateEmail(email) -> {
                input_email_register.error = "Email already exists"
                input_email_register.requestFocus()
                return
            }
            else -> {

                // Cleaning error messages

                input_user_register.error = null
                input_email_register.error = null
                input_password_register.error = null

                // Register user

                val registerUserValidation = Users (name = username, email = email, password = password)
                val confirmationRegisterUser = usersDAO.registerUser(registerUserValidation)
                if (confirmationRegisterUser) {
                    temporaryMessageCreateAccount()
                    val intentCreateAccountButton = Intent(this, LoginActivity::class.java)
                    startActivity(intentCreateAccountButton)
                    finish()
                } else {
                    Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Temporary messages

    private fun temporaryMessageGoogleLogin() {
        val toast = Toast.makeText(this, "Google registration", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 1000)
    }

    private fun temporaryMessageCreateAccount () {
        val toast = Toast.makeText(this, "Successful registration", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 1000)
    }
}
