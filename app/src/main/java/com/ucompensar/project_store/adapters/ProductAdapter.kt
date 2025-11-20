package com.ucompensar.project_store.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ucompensar.project_store.databinding.ItemProductBinding // <-- Usaremos este nombre para el layout individual
import com.ucompensar.project_store.models.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: MutableList<Product>,

    private val onQuantityChange: (Product, Int) -> Unit,

    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))


    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {

            binding.txtName.text = product.name
            binding.txtCategory.text = product.category
            binding.txtPrice.text = currencyFormat.format(product.price)
            binding.txtQuantity.text = product.quantity.toString()


            val context = binding.root.context
            val imageId = context.resources.getIdentifier(
                product.imageUrl,
                "drawable",
                context.packageName
            )
            if (imageId != 0) {
                binding.imgProduct.setImageResource(imageId)
            } else {

            }


            // boton de AUMENTAR
            binding.btnAdd.setOnClickListener {
                val newQuantity = product.quantity + 1
                // Ejecuta la función de callback de la Activity
                onQuantityChange(product, newQuantity)
                // Actualiza la vista inmediatamente
                binding.txtQuantity.text = newQuantity.toString()
            }

            // boton de DISMINUIR
            binding.btnSubtract.setOnClickListener {
                if (product.quantity > 0) {
                    val newQuantity = product.quantity - 1
                    onQuantityChange(product, newQuantity)
                    // Actualiza la vista inmediatamente
                    binding.txtQuantity.text = newQuantity.toString()
                }
            }

            // boton de ELIMINAR
            binding.btnDelete.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }


    override fun getItemCount(): Int = products.size
}