package com.ucompensar.project_store.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ucompensar.project_store.models.Product
import com.ucompensar.project_store.databinding.ItemProductFavoriteBinding
import java.text.NumberFormat
import java.util.Locale

class FavoriteProductAdapter(

    private var products: List<Product>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<FavoriteProductAdapter.FavoriteProductViewHolder>() {


    private var originalProducts: List<Product> = products.toList()

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    inner class FavoriteProductViewHolder(private val binding: ItemProductFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = "${currencyFormat.format(product.price)} COP"


            val imageUrl = product.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                val context = binding.root.context
                val imageId = context.resources.getIdentifier(
                    imageUrl,
                    "drawable",
                    context.packageName
                )
                binding.productImage.setImageResource(if (imageId != 0) imageId else android.R.drawable.ic_dialog_info)
            } else {
                binding.productImage.setImageResource(android.R.drawable.ic_dialog_info)
            }


            binding.buttonAdd.setOnClickListener {
                onAddClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductViewHolder {
        val binding = ItemProductFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size


    fun setOriginalList(allProducts: List<Product>) {
        this.originalProducts = allProducts.toList()

    }

    fun updateList(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }
}