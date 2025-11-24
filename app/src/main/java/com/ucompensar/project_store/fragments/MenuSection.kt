package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ucompensar.project_store.activities.SessionManager
import com.ucompensar.project_store.adapters.FavoriteProductAdapter
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.database.CartDAO
import com.ucompensar.project_store.databinding.FragmentMenuSectionBinding
import com.ucompensar.project_store.models.Product

class MenuSection : Fragment() {

    private var _binding: FragmentMenuSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var productDAO: ProductDAO
    private lateinit var cartDAO: CartDAO
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: FavoriteProductAdapter
    private var allProducts: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDAO = ProductDAO(requireContext())
        cartDAO = CartDAO(requireContext())
        sessionManager = SessionManager(requireContext())
        productDAO.checkAndSeedProducts()

        setupRecyclerView()
        loadAllProducts()

        binding.headerLayout.findViewById<View>(com.ucompensar.project_store.R.id.btnBack)?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.headerLayout.findViewById<View>(com.ucompensar.project_store.R.id.btnNotifications)?.setOnClickListener {
            navigateToCart()
        }
    }

    private fun setupRecyclerView() {
        adapter = FavoriteProductAdapter(
            products = allProducts,
            onAddClick = { product -> handleAddClick(product) },
            onDetailClick = { productId -> navigateToDetail(productId) }
        )
        binding.recyclerViewMenuProducts.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.recyclerViewMenuProducts.adapter = adapter
    }

    private fun loadAllProducts() {
        allProducts = productDAO.getAllProducts()

        adapter.updateList(allProducts)

        if (allProducts.isEmpty()) {
            binding.textViewNoProducts.visibility = View.VISIBLE
        } else {
            binding.textViewNoProducts.visibility = View.GONE
        }
    }

    private fun handleAddClick(product: Product) {
        val userId = if (sessionManager.isLoggedIn()) sessionManager.getUserId() else 1
        val added = cartDAO.addProductToCart(product, userId)

        if (added) {
            Toast.makeText(context, "${product.name} agregado al carrito.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error al agregar ${product.name} al carrito.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDetail(productId: Int) {
        try {
            val action = MenuSectionDirections.actionMenuToProductDetailSection(productId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, "Error de navegación (Menu): ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToCart() {
        try {
            val action = MenuSectionDirections.actionMenuToMarketCartSection()
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al ir al carrito: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}