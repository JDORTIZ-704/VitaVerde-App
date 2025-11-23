package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucompensar.project_store.adapters.FavoriteProductAdapter
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.FragmentMenuSectionBinding
import com.ucompensar.project_store.models.Product

class MenuSection : Fragment() {

    private var _binding: FragmentMenuSectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var productDAO: ProductDAO

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
        loadMenuProducts()
    }

    override fun onResume() {
        super.onResume()

        loadMenuProducts()
    }

    private fun loadMenuProducts() {

        val productList = productDAO.getAllProductsSortedByName()

        // 2. Configurar el RecyclerView con la lista ordenada
        setupRecyclerView(productList)
    }

    private fun setupRecyclerView(products: List<Product>) {


        val onAdd: (Product) -> Unit = { product ->

            Toast.makeText(context, "${product.name} agregado al carrito desde el Menú.", Toast.LENGTH_SHORT).show()

        }

        val adapter = FavoriteProductAdapter(products, onAdd)

        binding.recyclerViewMenuProducts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}