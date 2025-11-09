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
import com.ucompensar.project_store.MainActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.UsersDAO

class LoginActivity : AppCompatActivity() {

    private lateinit var input_mail_login: TextInputEditText
    private lateinit var input_password_login: TextInputEditText
    private lateinit var usersDAO: UsersDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize DAO

        usersDAO = UsersDAO(this)

        initializeView()
    }

    private fun initializeView() {

        // Inputs

        input_mail_login = findViewById(R.id.input_mail_login)
        input_password_login = findViewById(R.id.input_password_login)

        // Buttons

        val createUserButton: TextView = findViewById(R.id.btn_login_to_register)

        createUserButton.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        val googleButton: Button = findViewById(R.id.btn_google_get_into_user)

        googleButton.setOnClickListener {
            temporaryMessageEnterGoogle()
        }

        val loginButton: Button = findViewById(R.id.btn_create_user_login)

        loginButton.setOnClickListener {
            validateLogin()

        }

    }

    private fun validateLogin() {
        val mail = input_mail_login.text.toString().trim()
        val password = input_password_login.text.toString().trim()

        if (mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            mail.isEmpty() -> {
                input_mail_login.error = "Please enter a mail"
                input_mail_login.requestFocus()
                return
            }
            password.isEmpty() -> {
                input_password_login.error = "Please enter a password"
                input_password_login.requestFocus()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                input_mail_login.error = "Please enter a valid mail"
                input_mail_login.requestFocus()
                return
            }
            else -> {

                // Cleaning error messages

                input_mail_login.error = null
                input_password_login.error = null

                // Logging into the app

                if (usersDAO.validateLogin(mail, password)) {
                    temporaryMessageEnter()
                    val intent = Intent(this, SetLocationActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Temporary messages

    private fun temporaryMessageEnterGoogle () {
        val toast = Toast.makeText(this, "Google Login", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }

    private fun temporaryMessageEnter () {
        val toast = Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT)
        toast.show()

        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }

}
