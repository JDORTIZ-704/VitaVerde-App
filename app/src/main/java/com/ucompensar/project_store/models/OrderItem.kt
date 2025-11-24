package com.ucompensar.project_store.models

/**
 * Clase de modelo de datos para los ítems o Detalles de una Orden.
 * Corresponde a la tabla 'order_items'.
 *
 * @property id El ID de la base de datos (clave primaria, auto-incrementable). **(Cambiado a Long)**
 * @property orderId El ID de la orden a la que pertenece este ítem (Foreign Key a 'orders'). **(Cambiado a Long)**
 * @property productId El ID del producto comprado (Foreign Key a 'products').
 * @property quantity La cantidad de este producto comprado.
 * @property unitPrice El precio unitario del producto al momento de la compra (para evitar cambios si el precio del producto cambia después).
 */
data class OrderItem(
    val id: Long = 0,
    val orderId: Long,
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double
)