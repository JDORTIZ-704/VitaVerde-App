package com.ucompensar.project_store.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ucompensar.project_store.R
import com.ucompensar.project_store.activities.SessionManager
import com.ucompensar.project_store.database.CartDAO
import com.ucompensar.project_store.database.OrderDAO
import com.ucompensar.project_store.databinding.FragmentPaymentSectionBinding
import com.ucompensar.project_store.models.Order
import com.ucompensar.project_store.models.OrderItem
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PaymentSection : Fragment() {

    private var _binding: FragmentPaymentSectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartDAO: CartDAO
    private lateinit var orderDAO: OrderDAO
    private lateinit var sessionManager: SessionManager
    private var currentUserId: Int = -1
    private var cartTotal: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartDAO = CartDAO(requireContext())
        orderDAO = OrderDAO(requireContext())
        sessionManager = SessionManager(requireContext())
        currentUserId = sessionManager.getUserId().takeIf { it != -1 } ?: 1

        loadOrderTotal()

        binding.btnConfirm.setOnClickListener {
            processPurchase()
        }
        binding.headerLayout.findViewById<View>(R.id.btnBack).setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun loadOrderTotal() {
        val items = cartDAO.getCartItemsForUser(currentUserId)

        val subtotal = items.sumOf { it.price * it.quantity }
        val shippingCost = 3000.0
        cartTotal = subtotal + shippingCost

        val formattedTotal = formatCurrency(cartTotal)

        binding.txtTotalValue.text = formattedTotal
    }

    private fun processPurchase() {

        val cartItems = cartDAO.getCartItemsForUser(currentUserId)
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "El carrito está vacío, no se puede procesar la compra.", Toast.LENGTH_LONG).show()
            return
        }

        val orderItems = cartItems.map { item ->
            OrderItem(
                id = 0,
                orderId = 0,
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = item.price
            )
        }

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val formattedDate = currentDateTime.format(formatter)

        val newOrder = Order(
            id = 0,
            orderDate = formattedDate,
            total = cartTotal,
            status = "Pendiente",
            userId = currentUserId
        )

        val orderId = orderDAO.createOrder(newOrder, orderItems)

        if (orderId != -1L) {
            cartDAO.clearCart(currentUserId)

            findNavController().navigate(R.id.action_paymentSection_to_confirmationSection)

        } else {
            Toast.makeText(context, "Error al procesar el pago. Revise el stock.", Toast.LENGTH_LONG).show()
        }
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