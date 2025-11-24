package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucompensar.project_store.R
import com.ucompensar.project_store.activities.SessionManager
import com.ucompensar.project_store.adapters.CartAdapter
import com.ucompensar.project_store.database.CartDAO
import com.ucompensar.project_store.databinding.FragmentMarketCartSectionBinding
import com.ucompensar.project_store.models.CartItem
import java.text.NumberFormat
import java.util.Locale

class MarketCartSection : Fragment() {

    private var _binding: FragmentMarketCartSectionBinding? = null
    // Usar 'binding' de forma segura
    private val binding get() = _binding!!

    private lateinit var cartDAO: CartDAO
    private lateinit var sessionManager: SessionManager
    private lateinit var cartItemAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketCartSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartDAO = CartDAO(requireContext())
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        loadCartItems()
        setupListeners()
    }

    private fun setupRecyclerView() {
        cartItemAdapter = CartAdapter(
            onQuantityChange = { item, newQuantity -> onQuantityChanged(item, newQuantity) },
            onRemove = { item -> onItemRemoved(item) }
        )
        // ID corregido: recyclerViewCartItems
        binding.recyclerViewCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartItemAdapter
        }
    }

    private fun loadCartItems() {
        val userId = sessionManager.getUserId()
        // Método DAO corregido: getCartItemsForUser
        val cartItems = cartDAO.getCartItemsForUser(userId)
        // Método ListAdapter corregido: submitList
        cartItemAdapter.submitList(cartItems)
        updateTotalSummary(cartItems)
    }

    private fun updateTotalSummary(items: List<CartItem>) {
        var subtotal = 0.0
        items.forEach { item ->
            subtotal += item.price * item.quantity
        }
        val shippingCost = 3000.0 // Asumimos un costo fijo
        val total = subtotal + shippingCost

        val formattedSubtotal = formatCurrency(subtotal)
        val formattedTotal = formatCurrency(total)

        // IDs corregidos: txtSubtotalValue, txtTotalValue
        binding.txtSubtotalValue.text = formattedSubtotal
        // Si quieres mostrar el valor del envío:
        binding.txtShippingValue.text = formatCurrency(shippingCost)
        binding.txtTotalValue.text = formattedTotal
    }

    private fun setupListeners() {
        // ID corregido: btnPay
        binding.btnPay.setOnClickListener {
            try {
                // Navegación corregida (asumiendo que este es el ID correcto en NavGraph)
                findNavController().navigate(R.id.action_marketCartSection_to_paymentSection)
            } catch (e: IllegalArgumentException) {
                Log.e("MarketCartSection", "Error de navegación: verifica tu NavGraph.", e)
            }
        }

        // Agregar listener para el botón de regreso, si es necesario
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Agregar listener para la acción derecha (notificaciones)
        binding.btnActionRight.setOnClickListener {
            // Lógica de notificaciones o acción
        }
    }

    fun onQuantityChanged(cartItem: CartItem, newQuantity: Int) {
        // Método DAO corregido: updateItemQuantity
        cartDAO.updateItemQuantity(cartItem.cartItemId!!, newQuantity)
        loadCartItems()
    }

    fun onItemRemoved(cartItem: CartItem) {
        // Método DAO corregido: deleteItem
        cartDAO.deleteItem(cartItem.cartItemId!!)
        loadCartItems()
    }

    private fun formatCurrency(amount: Double): String {
        @Suppress("DEPRECATION")
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
        return format.format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}