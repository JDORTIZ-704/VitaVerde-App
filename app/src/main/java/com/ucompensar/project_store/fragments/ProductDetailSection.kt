package com.ucompensar.project_store.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ucompensar.project_store.activities.SessionManager
import com.ucompensar.project_store.database.CartDAO
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.FragmentProductDetailSectionBinding
import com.ucompensar.project_store.models.Product
import java.text.NumberFormat
import java.util.Locale

class ProductDetailSection : Fragment() {

    private var _binding: FragmentProductDetailSectionBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailSectionArgs by navArgs()
    private lateinit var productDAO: ProductDAO
    private lateinit var cartDAO: CartDAO
    private lateinit var sessionManager: SessionManager
    private lateinit var currentProduct: Product

    private val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale("es", "CO")
    ).apply {
        maximumFractionDigits = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDAO = ProductDAO(requireContext())
        cartDAO = CartDAO(requireContext())
        sessionManager = SessionManager(requireContext())

        val productId = args.productId

        if (productId != -1) {
            loadProductDetails(productId)
        } else {
            Toast.makeText(context, "Error: Producto no encontrado.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonAddToCartDetail.setOnClickListener {
            if (::currentProduct.isInitialized) {
                addProductToCart(currentProduct)
            }
        }
    }

    private fun loadProductDetails(productId: Int) {
        val product = productDAO.getProductById(productId)

        if (product != null) {
            currentProduct = product
            bindProductData(product)
        } else {
            Toast.makeText(context, "Producto con ID $productId no encontrado.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
    }

    private fun bindProductData(product: Product) {
        binding.productNameDetail.text = product.name
        binding.productDescription.text = product.description

        val benefitsText = product.shortDescription.split(", ").joinToString("\n")
        binding.productBenefits.text = benefitsText

        val formattedPrice = currencyFormat.format(product.price)
        binding.productPriceAction.text = "$formattedPrice COP por unidad"

        val imageUrl = product.imageUrl
        if (!imageUrl.isNullOrEmpty()) {
            val imageId = resources.getIdentifier(
                imageUrl,
                "drawable",
                requireContext().packageName
            )
            binding.productImageDetail.setImageResource(if (imageId != 0) imageId else R.drawable.ic_menu_help)
        }
    }

    private fun addProductToCart(product: Product) {
        val userId = if (sessionManager.isLoggedIn()) sessionManager.getUserId() else 1
        val added = cartDAO.addProductToCart(product, userId)

        if (added) {
            Toast.makeText(context, "${product.name} agregado al carrito.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error al agregar ${product.name} al carrito.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}