package com.ucompensar.project_store.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ucompensar.project_store.R
import org.w3c.dom.Text

class SetLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_location)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val optionsSpinner = arrayOf("Option 1", "Option 2", "Option 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, optionsSpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        val btn = findViewById<Button>(R.id.btn_setlocation_confirmar)

        btn.setOnClickListener {
            val selectedOption = spinner.selectedItem.toString()
            val textView : TextView? = findViewById(R.id.textView5)
            if (textView != null) {
                textView.text = selectedOption
            }
        }
    }
}