package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.ucompensar.project_store.R
import com.ucompensar.project_store.adapters.FavoriteProductAdapter
import com.ucompensar.project_store.database.ProductDAO
import com.ucompensar.project_store.databinding.FragmentFavoritesSectionBinding
import com.ucompensar.project_store.models.Product

class FavoritesSection : Fragment() {

    private var _binding: FragmentFavoritesSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var productDAO: ProductDAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDAO = ProductDAO(requireContext())
        productDAO.checkAndSeedProducts()


        val randomProducts = productDAO.getFiveRandomProducts()
        setupRecyclerView(randomProducts)


        binding.imageView2.setOnClickListener {

            findNavController().navigate(R.id.action_favoritesSection_to_Search)
        }


        binding.button.setOnClickListener {
            Toast.makeText(context, "Navegando a la vista completa de productos...", Toast.LENGTH_SHORT).show()


        }
    }

    private fun setupRecyclerView(products: List<Product>) {
        val adapter = FavoriteProductAdapter(products) { product ->
            addProductToCart(product)
        }

        binding.recyclerViewFavorites.apply {

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }

    private fun addProductToCart(product: Product) {
        Toast.makeText(context, "${product.name} agregado al carrito desde Home.", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}