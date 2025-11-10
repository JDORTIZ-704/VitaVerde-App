package com.ucompensar.project_store.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Text gradient

        val textView = findViewById<TextView>(R.id.txt_subtitle_3_splash)

        // We create the gradient (from top to bottom)
        val textShader = LinearGradient(
            0f, 0f, 0f, textView.textSize * textView.lineCount,
            intArrayOf(
                Color.parseColor("#53E88B"), // Color arriba (verde claro)
                Color.parseColor("#15BE77")  // Color abajo (verde oscuro)
            ),
            null,
            Shader.TileMode.CLAMP
        )

        // We assign the gradient to the text
        textView.paint.shader = textShader

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeActivity::class.java)
           startActivity(intent)
            finish()
        }, SPLASH_DURATION)
    }
}