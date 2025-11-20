package com.ucompensar.project_store.models

/**
 * Clase de modelo de datos para la Cabecera de una Orden (Pedido).
 * Corresponde a la tabla 'orders'.
 * * @property id El ID de la base de datos (clave primaria, auto-incrementable).
 * @property date La fecha de creación del pedido (guardada como String o DateTime).
 * @property total El monto total pagado por el pedido.
 * @property status El estado actual del pedido ("Pendiente", "Despachado", "Completado").
 * @property userId El ID del usuario que realizó el pedido (Foreign Key a la tabla 'users').
 */
data class Order(
    val id: Int = 0, // 0 por defecto si es una nueva inserción
    val date: String,
    val total: Double,
    var status: String,
    val userId: Int // Asumimos que el userId del cliente es un entero (el ID de SQLite)
)