package com.ucompensar.project_store.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucompensar.project_store.R
import com.ucompensar.project_store.adapters.AdminOrderAdapter
import com.ucompensar.project_store.database.OrderDAO
import com.ucompensar.project_store.databinding.ActivityAdminViewOrdersBinding
import com.ucompensar.project_store.models.Order
import com.ucompensar.project_store.models.OrderItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class AdminViewOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminViewOrdersBinding
    private lateinit var orderDAO: OrderDAO
    private lateinit var orderAdapter: AdminOrderAdapter
    private val TAG = "AdminOrders"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminViewOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // IMPORTANTE: Asegúrate de inicializar DataBaseHelper y ProductDAO
        // y de tener productos en la base de datos (con IDs 1, 2, 3, 4, 5) para que los datos dummy funcionen.

        orderDAO = OrderDAO(this)

        setupDummyData()
        setupRecyclerView()
        loadOrders()

        binding.headerLayout.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        binding.headerLayout.findViewById<ImageView>(R.id.btnRefresh).setOnClickListener {
            loadOrders()
            Toast.makeText(this, "Órdenes recargadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = AdminOrderAdapter(emptyList()) { order ->
            handleOrderDetailsClick(order)
        }
        binding.recyclerViewAdminOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAdminOrders.adapter = orderAdapter
    }

    private fun loadOrders() {
        val orders = orderDAO.getAllOrders()
        orderAdapter.updateOrders(orders)

        if (orders.isEmpty()) {
            binding.recyclerViewAdminOrders.visibility = View.GONE
            binding.textViewNoOrders.visibility = View.VISIBLE
        } else {
            binding.recyclerViewAdminOrders.visibility = View.VISIBLE
            binding.textViewNoOrders.visibility = View.GONE
        }
    }

    private fun handleOrderDetailsClick(order: Order) {
        Toast.makeText(this, "Ver detalle de Orden #${order.id}. Usuario: ${order.userId}", Toast.LENGTH_LONG).show()
        // Aquí puedes lanzar un Intent para una OrderDetailActivity, pasando order.id como extra.

        // Ejemplo de cómo obtener los ítems:
        val items = orderDAO.getOrderItemsByOrderId(order.id)
        Log.d(TAG, "Items para la orden ${order.id}: $items")
    }

    private fun setupDummyData() {
        // Solo inserta datos si no hay órdenes
        if (orderDAO.getAllOrders().isEmpty()) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            // Datos de prueba: Asume que existen productos con IDs del 1 al 5

            // ORDEN 1: Entregada
            val order1Items = listOf(
                OrderItem(orderId = 0, productId = 1, quantity = 2, unitPrice = 1500.0), // Ahuyama
                OrderItem(orderId = 0, productId = 2, quantity = 5, unitPrice = 2000.0)  // Banano
            )
            val order1Total = order1Items.sumOf { it.quantity * it.unitPrice }
            orderDAO.createOrder(
                Order(
                    orderDate = LocalDateTime.now().minusDays(3).format(formatter),
                    total = order1Total,
                    status = "Entregado",
                    userId = 101 // ID de usuario ficticio
                ), order1Items
            )

            // ORDEN 2: Procesada
            val order2Items = listOf(
                OrderItem(orderId = 0, productId = 3, quantity = 1, unitPrice = 3000.0), // Brócoli
                OrderItem(orderId = 0, productId = 4, quantity = 4, unitPrice = 1200.0)  // Zanahoria
            )
            val order2Total = order2Items.sumOf { it.quantity * it.unitPrice }
            orderDAO.createOrder(
                Order(
                    orderDate = LocalDateTime.now().minusHours(8).format(formatter),
                    total = order2Total,
                    status = "Procesado",
                    userId = 102
                ), order2Items
            )

            // ORDEN 3: Pendiente
            val order3Items = listOf(
                OrderItem(orderId = 0, productId = 5, quantity = 3, unitPrice = 4500.0) // Aguacate
            )
            val order3Total = order3Items.sumOf { it.quantity * it.unitPrice }
            orderDAO.createOrder(
                Order(
                    orderDate = LocalDateTime.now().format(formatter),
                    total = order3Total,
                    status = "Pendiente",
                    userId = 101
                ), order3Items
            )

            Log.d(TAG, "Datos de prueba insertados con éxito.")
        }
    }
}