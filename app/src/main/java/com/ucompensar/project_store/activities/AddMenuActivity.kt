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

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private lateinit var productDAO: ProductDAO
    private var imageUri: Uri? = null // URI temporal de la galería


    private val categories = mutableListOf("Fruta", "Verdura", "Otros", "Agregar Nueva...")
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val pickImageGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            binding.imgProductPreview.setImageURI(imageUri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDAO = ProductDAO(this)
        setupCategorySpinner()
        setupListeners()
    }



    private fun setupCategorySpinner() {
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductCategory.adapter = categoryAdapter

        binding.spinnerProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = categories[position]
                if (selectedItem == "Agregar Nueva...") {
                    showAddCategoryDialog()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita acción aquí
            }
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
        binding.imgBackButton.setOnClickListener {
            finish()
        }

        binding.btnAddImage.setOnClickListener {
            pickImageGallery()
        }

        binding.btnAddProduct.setOnClickListener {
            saveProduct()
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageGallery.launch(intent)
    }



    /**
     * Copia la imagen de la URI temporal al almacenamiento interno de la app.
     * @return La ruta absoluta del archivo guardado, o null si falla.
     */
    private fun saveImageToInternalStorage(uri: Uri): String? {
        val fileName = "product_${UUID.randomUUID()}.jpg"
        val outputDir = File(filesDir, "product_images")

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFile = File(outputDir, fileName)

        try {
            // Obtener un InputStream de la URI (la imagen seleccionada)
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            // Abrir un FileOutputStream para escribir en el archivo interno
            val outputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    // Copiar bytes del InputStream al FileOutputStream
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



    private fun saveProduct() {

        val name = binding.inputProductName.text.toString().trim()
        val priceText = binding.inputProductPrice.text.toString().trim()
        val description = binding.inputProductDescription.text.toString().trim()
        val shortDescription = binding.inputProductShortDescription.text.toString().trim()
        val quantityText = binding.inputProductQuantity.text.toString().trim()
        val category = binding.spinnerProductCategory.selectedItem.toString()


        if (category == "Agregar Nueva...") {
            Toast.makeText(this, "Selecciona una categoría válida.", Toast.LENGTH_LONG).show()
            return
        }
        if (name.isEmpty() || priceText.isEmpty() || description.isEmpty() || shortDescription.isEmpty() || imageUri == null || quantityText.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_LONG).show()
            return
        }


        val price: Double = try { priceText.toDouble() } catch (e: NumberFormatException) {
            Toast.makeText(this, "El precio debe ser un número válido.", Toast.LENGTH_LONG).show(); return
        }
        val quantity: Int = try { quantityText.toInt() } catch (e: NumberFormatException) {
            Toast.makeText(this, "La cantidad debe ser un número entero válido.", Toast.LENGTH_LONG).show(); return
        }


        val permanentImagePath = imageUri?.let { uri ->
            saveImageToInternalStorage(uri)
        }

        if (permanentImagePath.isNullOrEmpty()) {
            Toast.makeText(this, "Fallo al procesar la imagen. Producto no guardado.", Toast.LENGTH_LONG).show()
            return
        }


        val newProduct = Product(
            id = 0,
            name = name,
            category = category,
            price = price,
            description = description,
            shortDescription = shortDescription,
            imageUrl = permanentImagePath,
            quantity = quantity
        )


        val productId = productDAO.addProduct(newProduct)

        if (productId > 0) {
            Toast.makeText(this, "Producto '$name' agregado exitosamente!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Error: Falló al agregar el producto. Intenta de nuevo.", Toast.LENGTH_LONG).show()
        }
    }
}