package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        // Initialize the button

        val btnEnterWelcome: Button = findViewById(R.id.btn_enter_welcome)

        // Set the click listener for the button and initialize an Intent to redirect to the LoginActivity.

        btnEnterWelcome.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}