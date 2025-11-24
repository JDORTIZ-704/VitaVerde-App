package com.ucompensar.project_store.models

/**
 * Modelo para un ítem en el carrito.
 * Utiliza los datos del producto para mostrar en la interfaz de usuario.
 */
data class CartItem(
    val cartItemId: Long?,
    val productId: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageUrl: String
)