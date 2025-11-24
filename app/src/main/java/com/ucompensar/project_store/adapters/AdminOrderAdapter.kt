package com.ucompensar.project_store.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ucompensar.project_store.databinding.ItemOrderAdminBinding
import com.ucompensar.project_store.models.Order
import java.text.NumberFormat
import java.util.Locale

class AdminOrderAdapter(
    private var orders: List<Order>,
    private val onDetailsClick: (Order) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(private val binding: ItemOrderAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        // Formato para moneda colombiana (COP)
        private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

        fun bind(order: Order, onDetailsClick: (Order) -> Unit) {
            // Usa el ID Long de la orden
            binding.textViewOrderId.text = order.id.toString()
            binding.textViewOrderDate.text = "Fecha: ${order.orderDate}"
            // Usa el ID Int del usuario
            binding.textViewUserId.text = "Usuario ID: ${order.userId}"
            binding.textViewTotalAmount.text = numberFormat.format(order.total)
            binding.textViewOrderStatus.text = order.status

            setOrderStatusStyle(order.status)

            binding.buttonViewDetails.setOnClickListener {
                onDetailsClick(order)
            }
        }

        private fun setOrderStatusStyle(status: String) {
            val color: Int

            when (status.lowercase(Locale.ROOT)) {
                "pendiente" -> {
                    color = Color.parseColor("#FFC107") // Amarillo
                }
                "procesado" -> {
                    color = Color.parseColor("#2196F3") // Azul
                }
                "entregado" -> {
                    color = Color.parseColor("#4CAF50") // Verde
                }
                "cancelado" -> {
                    color = Color.parseColor("#F44336") // Rojo
                }
                else -> {
                    color = Color.parseColor("#757575") // Gris por defecto
                }
            }
            binding.textViewOrderStatus.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position], onDetailsClick)
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}