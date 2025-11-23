package com.ucompensar.project_store.models

/**
 * Clase de modelo de datos para un Producto.
 * @property id El ID de la base de datos (clave primaria, auto-incrementable).
 * @property name Nombre del producto (ej: "ahuyama").
 * @property category Categoría del producto (ej: "verdura").
 * @property price Precio del producto (usamos Double/REAL en SQLite).
 * @property quantity Cantidad disponible en inventario (usamos Int/INTEGER).
 * @property imageUrl URL o referencia a la imagen (podría ser un String, path o URI).
 */
data class Product(
    val id: Int = 0,
    val name: String,
    val category: String,
    val price: Double,
    var quantity: Int,
    val imageUrl: String,
    val description: String,
    val shortDescription: String
)