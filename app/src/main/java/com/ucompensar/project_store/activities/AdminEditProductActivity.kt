package com.ucompensar.project_store.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ucompensar.project_store.R
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.ActivityAddMenuBinding
import com.ucompensar.project_store.models.Product
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class AdminEditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private lateinit var productDAO: ProductDAO
    private lateinit var originalProduct: Product
    private var newImageUri: Uri? = null
    private var currentImagePath: String? = null

    private val categories = mutableListOf("Fruta", "Verdura", "Otros", "Agregar Nueva...")
    private lateinit var categoryAdapter: ArrayAdapter<String>


    private val pickImageGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            newImageUri = data?.data
            binding.imgProductPreview.setImageURI(newImageUri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDAO = ProductDAO(this)


        val productId = intent.getIntExtra("product_id_to_edit", -1)

        if (productId == -1) {
            Toast.makeText(this, "Error: ID de producto no encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        originalProduct = productDAO.getProductById(productId) ?: run {
            Toast.makeText(this, "Error: Producto no existe en BD.", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        currentImagePath = originalProduct.imageUrl

        setupCategorySpinner()

        loadProductData(originalProduct)
        setupListeners()


        binding.txtTitleAddProduct.text = getString(R.string.title_edit_product)

        binding.btnAddProduct.text = getString(R.string.btn_save_changes)
    }


    private fun setupCategorySpinner() {
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductCategory.adapter = categoryAdapter

        binding.spinnerProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = categories[position]
                if (selectedItem == "Agregar Nueva...") {
                    showAddCategoryDialog()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadProductData(product: Product) {
        binding.inputProductName.setText(product.name)
        binding.inputProductPrice.setText(product.price.toString())
        binding.inputProductQuantity.setText(product.quantity.toString())
        binding.inputProductDescription.setText(product.description)
        binding.inputProductShortDescription.setText(product.shortDescription)

        if (product.imageUrl.startsWith("/")) {
            binding.imgProductPreview.setImageURI(Uri.parse(product.imageUrl))
        }

        val categoryIndex = categories.indexOf(product.category)
        if (categoryIndex != -1) {
            binding.spinnerProductCategory.setSelection(categoryIndex)
        }
    }

    private fun showAddCategoryDialog() {
        val input = EditText(this)
        input.hint = getString(R.string.hint_new_category)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title_new_category))
            .setView(input)
            .setPositiveButton(getString(R.string.dialog_button_save)) { dialog, _ ->
                val newCategory = input.text.toString().trim()
                if (newCategory.isNotEmpty()) {
                    addCategory(newCategory)
                } else {
                    Toast.makeText(this, "La categoría no puede estar vacía.", Toast.LENGTH_SHORT).show()
                    binding.spinnerProductCategory.setSelection(0)
                }
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel)) { dialog, _ ->
                dialog.cancel()
                binding.spinnerProductCategory.setSelection(0)
            }
            .show()
    }

    private fun addCategory(newCategory: String) {
        if (newCategory !in categories) {
            val addIndex = categories.indexOf("Agregar Nueva...")
            if (addIndex != -1) {
                categories.add(addIndex, newCategory)
            } else {
                categories.add(newCategory)
            }
            categoryAdapter.notifyDataSetChanged()
        }
        binding.spinnerProductCategory.setSelection(categories.indexOf(newCategory))
    }

    private fun setupListeners() {
        binding.imgBackButton.setOnClickListener { finish() }

        // RESUELVE: Unresolved reference 'pickImageGallery'
        binding.btnAddImage.setOnClickListener { pickImageGallery.launch(Intent(Intent.ACTION_PICK).setType("image/*")) }

        // RESUELVE: Unresolved reference 'updateProduct'
        binding.btnAddProduct.setOnClickListener {
            updateProduct()
        }
    }

    // ----------------------------------------------------
    // LÓGICA DE IMAGEN Y ACTUALIZACIÓN
    // ----------------------------------------------------

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val fileName = "product_${UUID.randomUUID()}.jpg"
        val outputDir = File(filesDir, "product_images")

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFile = File(outputDir, fileName)

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return outputFile.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la imagen: ${e.message}", Toast.LENGTH_LONG).show()
            return null
        }
    }


    private fun updateProduct() {
        // 1. Obtención y validación de datos
        val name = binding.inputProductName.text.toString().trim()
        val priceText = binding.inputProductPrice.text.toString().trim()
        val description = binding.inputProductDescription.text.toString().trim()
        val shortDescription = binding.inputProductShortDescription.text.toString().trim()
        val quantityText = binding.inputProductQuantity.text.toString().trim()
        val category = binding.spinnerProductCategory.selectedItem.toString()

        if (category == "Agregar Nueva..." || name.isEmpty() || priceText.isEmpty() || description.isEmpty() || shortDescription.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        val price: Double = try { priceText.toDouble() } catch (e: NumberFormatException) {
            Toast.makeText(this, "El precio debe ser un número válido.", Toast.LENGTH_LONG).show(); return
        }
        val quantity: Int = try { quantityText.toInt() } catch (e: NumberFormatException) {
            Toast.makeText(this, "La cantidad debe ser un número entero válido.", Toast.LENGTH_LONG).show(); return
        }


        val finalImagePath = newImageUri?.let { uri ->
            saveImageToInternalStorage(uri)
        } ?: originalProduct.imageUrl

        if (finalImagePath.isNullOrEmpty()) {
            Toast.makeText(this, "La imagen del producto no es válida.", Toast.LENGTH_LONG).show()
            return
        }


        val updatedProduct = Product(
            id = originalProduct.id,
            name = name,
            category = category,
            price = price,
            description = description,
            shortDescription = shortDescription,
            imageUrl = finalImagePath,
            quantity = quantity
        )


        if (productDAO.updateProduct(updatedProduct)) {
            Toast.makeText(this, "Producto actualizado exitosamente!", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error: Falló al actualizar el producto. Intenta de nuevo.", Toast.LENGTH_LONG).show()
        }
    }
}