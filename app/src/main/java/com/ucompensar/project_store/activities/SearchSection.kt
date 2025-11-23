package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucompensar.project_store.adapters.FavoriteProductAdapter
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.FragmentSearchSectionBinding
import com.ucompensar.project_store.models.Product
import java.util.Locale

class SearchSection : Fragment() {

    private var _binding: FragmentSearchSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var productDao: ProductDAO

    private var allProducts: List<Product> = emptyList()
    private lateinit var adapter: FavoriteProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDao = ProductDAO(requireContext())
        productDao.checkAndSeedProducts()


        allProducts = productDao.getAllProducts()

        setupRecyclerView()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        val onAdd: (Product) -> Unit = { product ->
            addProductToCart(product)
        }

        adapter = FavoriteProductAdapter(allProducts, onAdd)

        adapter.setOriginalList(allProducts)

        binding.recyclerViewSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SearchSection.adapter
        }
    }

    private fun setupSearchListener() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        val filteredList = if (query.isBlank()) {
            allProducts
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())

            allProducts.filter { product ->
                product.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||

                        (product.shortDescription?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true) ||
                        (product.category?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true)
            }
        }


        if (filteredList.isEmpty() && !query.isBlank()) {
            Toast.makeText(context, "No se encontraron productos para \"$query\"", Toast.LENGTH_SHORT).show()
        }

        adapter.updateList(filteredList)
    }

    private fun addProductToCart(product: Product) {

        Toast.makeText(context, "${product.name} agregado al carrito desde Búsqueda.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}