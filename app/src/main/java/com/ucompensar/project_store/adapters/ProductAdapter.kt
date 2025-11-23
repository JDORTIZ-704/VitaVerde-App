package com.ucompensar.project_store.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ucompensar.project_store.R
import com.ucompensar.project_store.databinding.ItemProductBinding
import com.ucompensar.project_store.models.Product
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast

/**
 * Adaptador utilizado en la vista de administración de productos (AdminViewProductsActivity).
 * Permite la gestión directa de cantidad, edición y eliminación de productos.
 * * @param products Lista mutable de productos a mostrar.
 * @param onQuantityChange Callback para manejar cambios en la cantidad.
 * @param onDeleteClick Callback para manejar la eliminación de un producto.
 * @param onEditClick Callback para manejar la edición de un producto.
 */
class ProductAdapter(
    private val products: MutableList<Product>,
    private val onQuantityChange: (Product, Int) -> Unit,
    private val onDeleteClick: (Product) -> Unit,
    private val onEditClick: (Product) -> Unit // Callback de Edición
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))


    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun updateList(newList: List<Product>) {
            products.clear()
            products.addAll(newList)
            notifyDataSetChanged()
        }
        fun bind(product: Product) {

            binding.txtName.text = product.name
            binding.txtCategory.text = product.category
            binding.txtPrice.text = currencyFormat.format(product.price)
            binding.txtQuantity.text = product.quantity.toString()

            val imageUrl = product.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                if (imageUrl.startsWith("/")) {
                    binding.imgProduct.setImageURI(Uri.parse(imageUrl))
                } else {

                    val context = binding.root.context
                    val imageId = context.resources.getIdentifier(
                        imageUrl,
                        "drawable",
                        context.packageName
                    )
                    if (imageId != 0) {
                        binding.imgProduct.setImageResource(imageId)
                    } else {

                        binding.imgProduct.setImageResource(android.R.drawable.ic_menu_help)
                    }
                }
            } else {

                binding.imgProduct.setImageResource(android.R.drawable.ic_menu_help)
            }


            binding.btnAdd.setOnClickListener {
                val newQuantity = product.quantity + 1
                onQuantityChange(product, newQuantity)
                binding.txtQuantity.text = newQuantity.toString()
            }


            binding.btnSubtract.setOnClickListener {
                if (product.quantity > 0) {
                    val newQuantity = product.quantity - 1
                    onQuantityChange(product, newQuantity)
                    binding.txtQuantity.text = newQuantity.toString()
                } else {
                    Toast.makeText(binding.root.context, "La cantidad mínima es 0.", Toast.LENGTH_SHORT).show()
                }
            }


            binding.btnDelete.setOnClickListener {
                onDeleteClick(product)
            }


            binding.btnEdit.setOnClickListener {
                onEditClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {

        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}