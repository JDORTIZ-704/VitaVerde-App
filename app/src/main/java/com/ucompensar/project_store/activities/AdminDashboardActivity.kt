package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMetrics()
        setupActionButtons()
    }

    private fun initializeMetrics() {
        binding.txtPendingCount.text = "30"
        binding.txtCompletedCount.text = "10"
        binding.txtEarningsAmount.text = "3200$"
    }

    /**
     * Configura los OnClickListeners para cada CardView, usando IDs distintos.
     */
    private fun setupActionButtons() {

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewProducts.setOnClickListener {
            val intent = Intent(this, AdminViewProductsActivity::class.java)
            startActivity(intent)
        }


        binding.btnProfiles.setOnClickListener {
            Toast.makeText(this, "Navegando a Perfiles", Toast.LENGTH_SHORT).show()
        }


        binding.btnCreateUser.setOnClickListener {
            val intent = Intent(this, AdminCreateUserActivity::class.java)
            startActivity(intent)
        }


        binding.btnDispatchedOrder.setOnClickListener {
            Toast.makeText(this, "Navegando a Ver Órdenes", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
        }
    }
}