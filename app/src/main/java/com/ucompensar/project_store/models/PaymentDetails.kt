package com.ucompensar.project_store.models

/**
 * Modelo para almacenar la información de envío y pago durante el proceso de checkout.
 */
data class PaymentDetails(
    val name: String,
    val address: String,
    val phone: String,
    val paymentMethod: String // Ejemplo: "PSE"
)