package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ucompensar.project_store.activities.SessionManager
import com.ucompensar.project_store.adapters.FavoriteProductAdapter
import com.ucompensar.project_store.database.CartDAO
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.FragmentSearchSectionBinding
import com.ucompensar.project_store.models.Product

class SearchSection : Fragment() {

    private var _binding: FragmentSearchSectionBinding? = null
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
        _binding = FragmentSearchSectionBinding.inflate(inflater, container, false)
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
        setupSearchListener() // Nuevo listener para el campo de búsqueda
    }

    private fun setupRecyclerView() {
        adapter = FavoriteProductAdapter(
            products = allProducts, // Inicialmente vacío
            onAddClick = { product -> handleAddClick(product) },
            onDetailClick = { productId -> navigateToDetail(productId) }
        )
        binding.recyclerViewSearch.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.recyclerViewSearch.adapter = adapter
    }

    private fun loadAllProducts() {

        allProducts = productDAO.getAllProducts()


        adapter.updateList(allProducts)

        if (allProducts.isEmpty()) {
            binding.textViewNoResults.visibility = View.VISIBLE
        } else {
            binding.textViewNoResults.visibility = View.GONE
        }
    }

    private fun setupSearchListener() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text change
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed during text change
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                filterProducts(query)
            }
        })
    }

    private fun filterProducts(query: String) {
        if (query.isBlank()) {

            adapter.updateList(allProducts)
            binding.textViewNoResults.visibility = if (allProducts.isEmpty()) View.VISIBLE else View.GONE
        } else {

            val filteredList = allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }

            adapter.updateList(filteredList)

            if (filteredList.isEmpty()) {

                binding.textViewNoResults.text = "No se encontraron resultados para \"$query\""
                binding.textViewNoResults.visibility = View.VISIBLE
            } else {
                binding.textViewNoResults.visibility = View.GONE
            }
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

            val action = SearchSectionDirections.actionSearchToProductDetailSection(productId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(context, "Error de navegación (Search): ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}