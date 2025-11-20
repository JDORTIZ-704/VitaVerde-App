package com.ucompensar.project_store.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucompensar.project_store.adapters.ProductAdapter
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.ActivityAdminViewProductsBinding // <-- Asume que el binding se llama así
import com.ucompensar.project_store.models.Product

class AdminViewProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminViewProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productDAO: ProductDAO // <-- Nueva instancia para la DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La referencia a la clase de binding se resuelve con la importación:
        binding = ActivityAdminViewProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inicializar el DAO
        productDAO = ProductDAO(this)

        // 2. Insertar datos de prueba si no existen
        productDAO.checkAndSeedProducts()

        // 3. Botón de regreso (Corregida la referencia a 'btnBack')
        // Asumiendo que 'btnBack' es el ID de la flecha de regreso en tu layout
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 4. Cargar datos de productos desde la BD
        val products = productDAO.getAllProducts().toMutableList() // Usar lista mutable

        // 5. Configuración del RecyclerView
        setupRecyclerView(products)
    }

    /**
     * Configura el RecyclerView con el adaptador y la lista de productos.
     */
    private fun setupRecyclerView(products: MutableList<Product>) {
        // Inicializar el adaptador con la lista y los listeners
        productAdapter = ProductAdapter(products,
            onQuantityChange = { product, newQuantity ->
                // Actualizar la cantidad en la base de datos
                if (productDAO.updateProductQuantity(product.id, newQuantity)) {
                    product.quantity = newQuantity // Actualiza el modelo
                    // Nota: el adaptador debe notificar el cambio si no se hace en el bind
                } else {
                    Toast.makeText(this, "Error al actualizar la cantidad", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { product ->
                // Lógica para eliminar el producto
                if (productDAO.deleteProduct(product.id)) {
                    // Eliminar de la lista local y notificar al adaptador
                    val position = products.indexOf(product)
                    if (position != -1) {
                        products.removeAt(position)
                        productAdapter.notifyItemRemoved(position)
                        Toast.makeText(this, "Producto ${product.name} eliminado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@AdminViewProductsActivity)
            adapter = productAdapter
        }
    }
}