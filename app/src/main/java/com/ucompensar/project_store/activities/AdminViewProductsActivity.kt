package com.ucompensar.project_store.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucompensar.project_store.adapters.ProductAdapter
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.ActivityAdminViewProductsBinding
import com.ucompensar.project_store.models.Product

class AdminViewProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminViewProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productDAO: ProductDAO
    private lateinit var productList: MutableList<Product>

    // Lanzador para esperar resultados de la Activity de Edición
    private val editProductLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Si la edición fue exitosa, recargamos los datos para actualizar la lista
                loadProducts()
                Toast.makeText(this, "Lista de productos actualizada.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialización del ViewBinding
        binding = ActivityAdminViewProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDAO = ProductDAO(this)

        setupListeners()
        loadProducts()
    }

    private fun setupListeners() {
        // RESUELVE: Unresolved reference 'btnBack' (si el ID en XML es correcto)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadProducts() {
        // Cargar datos de productos desde la BD
        productList = productDAO.getAllProducts().toMutableList()

        if (!::productAdapter.isInitialized) {
            setupRecyclerView(productList)
        } else {
            // Llama a la función updateList (debe existir en ProductAdapter.kt)
            // RESUELVE: Unresolved reference 'updateList'
            productAdapter.updateList(productList)
        }
    }

    private fun setupRecyclerView(products: MutableList<Product>) {

        // CALLBACK: Solo pasa el ID del producto (Int) para la edición
        val onEditClick: (Product) -> Unit = { productToEdit ->
            val intent = Intent(this, AdminEditProductActivity::class.java).apply {
                putExtra("product_id_to_edit", productToEdit.id)
            }
            editProductLauncher.launch(intent)
        }

        productAdapter = ProductAdapter(products,
            onQuantityChange = { product, newQuantity ->
                if (productDAO.updateProductQuantity(product.id, newQuantity)) {
                    product.quantity = newQuantity
                } else {
                    Toast.makeText(this, "Error al actualizar la cantidad", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { product ->
                if (productDAO.deleteProduct(product.id)) {
                    val position = products.indexOf(product)
                    if (position != -1) {
                        products.removeAt(position)
                        productAdapter.notifyItemRemoved(position)
                        Toast.makeText(this, "Producto ${product.name} eliminado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            },
            onEditClick = onEditClick
        )

        // RESUELVE: Unresolved reference 'recyclerViewProducts', 'layoutManager', 'adapter'
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@AdminViewProductsActivity)
            adapter = productAdapter
        }
    }
}