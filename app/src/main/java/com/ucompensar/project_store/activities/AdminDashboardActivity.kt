package com.ucompensar.project_store.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    // Declaración de la variable de View Binding
    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialización del View Binding, enlazando la actividad con el layout XML
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar los datos de las métricas (ej. 30, 10, 3200$)
        initializeMetrics()

        // Configurar los botones de acción del grid
        setupActionButtons()
    }

    /**
     * Establece los valores de las métricas.
     */
    private fun initializeMetrics() {
        // Acceso directo a las vistas de TextView definidas en activity_admin_dashboard.xml
        binding.txtPendingCount.text = "30"
        binding.txtCompletedCount.text = "10"
        binding.txtEarningsAmount.text = "3200$"
    }

    /**
     * Configura los OnClickListeners para cada CardView.
     */
    private fun setupActionButtons() {
        // Botón 1: Agregar Producto
        binding.btnAddProduct.setOnClickListener {
            Toast.makeText(this, "Navegando a Agregar Producto", Toast.LENGTH_SHORT).show()
        }

        // Botón 2: Productos
        binding.btnViewProducts.setOnClickListener {
            val intent = Intent(this, AdminViewProductsActivity::class.java)
            startActivity(intent)
        }

        // Botón 3: Perfiles
        binding.btnProfiles.setOnClickListener {
            Toast.makeText(this, "Navegando a Perfiles", Toast.LENGTH_SHORT).show()
        }

        // Botón 4: Crear Nuevo Usuario (Navegación activa)
        binding.btnCreateUser.setOnClickListener {
            val intent = Intent(this, AdminCreateUserActivity::class.java)
            startActivity(intent)
        }

        // Botón 5: Pedidos (Órdenes)
        binding.btnDispatchedOrder.setOnClickListener {
            Toast.makeText(this, "Navegando a Ver Órdenes", Toast.LENGTH_SHORT).show()
        }

        // Botón 6: Salir (Logout)
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
        }
    }
}