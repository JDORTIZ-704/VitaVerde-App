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
import com.ucompensar.project_store.R

class CreateUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_user)

        // Buttons

        val createAccountButton: Button = findViewById(R.id.btn_create_user_register)
        createAccountButton.setOnClickListener {
            val intentCreateAccountButton = Intent(this, LoginActivity::class.java)
            startActivity(intentCreateAccountButton)
            finish()
            temporaryMessageCreateAccount()
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