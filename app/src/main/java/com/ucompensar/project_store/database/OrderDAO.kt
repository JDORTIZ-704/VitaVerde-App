package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ucompensar.project_store.models.Order
import com.ucompensar.project_store.models.OrderItem
import com.ucompensar.project_store.models.Product // Para manejar el stock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDAO(context: Context) {

    private val dbHelper = DataBaseHelper(context)
    private val productDAO = ProductDAO(context) // Necesario para actualizar el stock

    //************************//
    //                        //
    // CREAR NUEVO PEDIDO     //
    //                        //
    //************************//

    /**
     * Crea un nuevo pedido, inserta sus ítems y actualiza el stock de los productos.
     * @return El ID de la orden insertada, o -1 si falla.
     */
    fun createOrder(order: Order, items: List<OrderItem>): Long {
        val db = dbHelper.writableDatabase
        var orderId: Long = -1L

        // Usamos una transacción para asegurar que la orden y todos sus ítems se inserten,
        // y que el stock se actualice, o que todo se revierta si hay un error.
        db.beginTransaction()
        try {
            // 1. Insertar la Cabecera de la Orden
            val orderValues = ContentValues().apply {
                put(DataBaseHelper.COLUMN_ORDER_DATE, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                put(DataBaseHelper.COLUMN_ORDER_TOTAL, order.total)
                put(DataBaseHelper.COLUMN_ORDER_STATUS, "Pendiente") // Estado inicial
                put(DataBaseHelper.COLUMN_ORDER_USER_ID, order.userId)
            }
            orderId = db.insert(DataBaseHelper.TABLE_ORDERS, null, orderValues)

            if (orderId != -1L) {
                // 2. Insertar los Ítems de la Orden y Actualizar el Stock
                for (item in items) {
                    val itemValues = ContentValues().apply {
                        put(DataBaseHelper.COLUMN_ITEM_ORDER_ID, orderId)
                        put(DataBaseHelper.COLUMN_ITEM_PRODUCT_ID, item.productId)
                        put(DataBaseHelper.COLUMN_ITEM_QUANTITY, item.quantity)
                        put(DataBaseHelper.COLUMN_ITEM_UNIT_PRICE, item.unitPrice)
                    }
                    val itemResult = db.insert(DataBaseHelper.TABLE_ORDER_ITEMS, null, itemValues)

                    if (itemResult == -1L) {
                        // Si falla la inserción de un ítem, forzamos la excepción
                        throw Exception("Fallo al insertar ítem de orden.")
                    }

                    // 3. Actualizar el stock del producto
                    val successStockUpdate = updateProductStock(db, item.productId, item.quantity)
                    if (!successStockUpdate) {
                        throw Exception("Fallo al actualizar el stock del producto ${item.productId}.")
                    }
                }
            } else {
                throw Exception("Fallo al insertar la cabecera de la orden.")
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
            orderId = -1L
        } finally {
            db.endTransaction()
            db.close()
        }
        return orderId
    }

    /**
     * Función auxiliar para actualizar el stock de un producto durante la transacción.
     */
    private fun updateProductStock(db: SQLiteDatabase, productId: Int, quantitySold: Int): Boolean {
        // Obtenemos el stock actual del producto (usando raw query para no abrir y cerrar otra conexión)
        val cursor = db.rawQuery(
            "SELECT ${DataBaseHelper.COLUMN_PRODUCT_QUANTITY} FROM ${DataBaseHelper.TABLE_PRODUCTS} WHERE ${DataBaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )

        var currentStock = 0
        if (cursor.moveToFirst()) {
            currentStock = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_QUANTITY))
        }
        cursor.close()

        val newStock = currentStock - quantitySold
        if (newStock < 0) return false // No se puede tener stock negativo

        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PRODUCT_QUANTITY, newStock)
        }

        val rowsAffected = db.update(
            DataBaseHelper.TABLE_PRODUCTS,
            values,
            "${DataBaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
        return rowsAffected > 0
    }

    //************************//
    //                        //
    // OBTENER ÓRDENES        //
    //                        //
    //************************//

    /**
     * Obtiene una lista de todas las órdenes, útil para el dashboard del administrador.
     */
    fun getAllOrders(): List<Order> {
        val orderList = mutableListOf<Order>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_ORDERS} ORDER BY ${DataBaseHelper.COLUMN_ORDER_DATE} DESC"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ORDER_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ORDER_DATE))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ORDER_TOTAL))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ORDER_STATUS))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ORDER_USER_ID))

                orderList.add(Order(id, date, total, status, userId))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return orderList
    }

    //************************//
    //                        //
    // ACTUALIZAR ESTADO      //
    //                        //
    //************************//

    /**
     * Actualiza el estado de una orden.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    fun updateOrderStatus(orderId: Int, newStatus: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_ORDER_STATUS, newStatus)
        }

        val rowsAffected = db.update(
            DataBaseHelper.TABLE_ORDERS,
            values,
            "${DataBaseHelper.COLUMN_ORDER_ID} = ?",
            arrayOf(orderId.toString())
        )
        db.close()
        return rowsAffected > 0
    }

    //************************//
    //                        //
    // OBTENER ITEMS DE ORDEN //
    //                        //
    //************************//

    /**
     * Obtiene todos los ítems de una orden específica.
     */
    fun getOrderItemsByOrderId(orderId: Int): List<OrderItem> {
        val itemList = mutableListOf<OrderItem>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DataBaseHelper.TABLE_ORDER_ITEMS} 
            WHERE ${DataBaseHelper.COLUMN_ITEM_ORDER_ID} = ?
        """

        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ITEM_ID))
                val productId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ITEM_PRODUCT_ID))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ITEM_QUANTITY))
                val unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ITEM_UNIT_PRICE))

                itemList.add(OrderItem(id, orderId, productId, quantity, unitPrice))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return itemList
    }
}