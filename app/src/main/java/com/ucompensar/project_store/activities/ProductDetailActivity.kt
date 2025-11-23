package com.ucompensar.project_store.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.ActivityProductDetailBinding
import com.ucompensar.project_store.models.Product
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productDAO: ProductDAO
    private lateinit var currentProduct: Product


    private val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale("es", "CO")
    ).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDAO = ProductDAO(this)

        val productId = intent.getIntExtra("product_id", -1)

        if (productId != -1) {
            loadProductDetails(productId)
        } else {
            Toast.makeText(this, "Error: Producto no encontrado.", Toast.LENGTH_LONG).show()
            finish()
        }

        setupListeners()
    }

    private fun setupListeners() {

        binding.headerLayout.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.bottomActionLayout.findViewById<Button>(R.id.buttonAddToCartDetail).setOnClickListener {
            if (::currentProduct.isInitialized) {
                Toast.makeText(this, "${currentProduct.name} agregado al carrito.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProductDetails(productId: Int) {
        val product = productDAO.getProductById(productId)

        if (product != null) {
            currentProduct = product
            bindProductData(product)
        } else {
            Toast.makeText(this, "Producto con ID $productId no encontrado.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun bindProductData(product: Product) {
        binding.productNameDetail.text = product.name
        binding.productDescription.text = product.description

        val benefitsText = product.shortDescription.split(", ").joinToString("\n")
        binding.productBenefits.text = benefitsText


        val formattedPrice = currencyFormat.format(product.price)
        binding.bottomActionLayout.findViewById<TextView>(R.id.productPriceAction).text = "$formattedPrice COP por unidad"

        // Imagen
        val imageUrl = product.imageUrl
        if (!imageUrl.isNullOrEmpty()) {
            val imageId = resources.getIdentifier(
                imageUrl,
                "drawable",
                packageName
            )

            binding.cardImageDetail.findViewById<ImageView>(R.id.productImageDetail).setImageResource(if (imageId != 0) imageId else android.R.drawable.ic_menu_help)
        }
    }
}