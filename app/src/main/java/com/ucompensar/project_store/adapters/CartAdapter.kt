package com.ucompensar.project_store.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ucompensar.project_store.R
import com.ucompensar.project_store.databinding.ItemCartProductBinding // Asumimos este layout
import com.ucompensar.project_store.models.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.textViewProductName.text = item.name
            binding.textViewProductPrice.text = formatPrice(item.price)
            binding.textViewQuantity.text = item.quantity.toString()


            Glide.with(binding.imageViewProduct.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_product_placeholder) // Placeholder genérico
                .into(binding.imageViewProduct)


            binding.buttonIncrease.setOnClickListener {
                onQuantityChange(item, item.quantity + 1)
            }


            binding.buttonDecrease.setOnClickListener {
                // Si la cantidad es 1, el botón de Decrementar actuará como eliminar
                if (item.quantity > 1) {
                    onQuantityChange(item, item.quantity - 1)
                } else {
                    onRemove(item)
                }
            }


            binding.buttonRemove.setOnClickListener {
                onRemove(item)
            }
        }

        private fun formatPrice(price: Double): String {
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            format.maximumFractionDigits = 0
            return format.format(price)
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.cartItemId == newItem.cartItemId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}