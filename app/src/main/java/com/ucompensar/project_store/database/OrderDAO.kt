package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.ucompensar.project_store.models.Order
import com.ucompensar.project_store.models.OrderItem
import com.ucompensar.project_store.database.DataBaseHelper.Companion.TABLE_ORDERS
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ORDER_ID
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ORDER_DATE
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ORDER_TOTAL
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ORDER_STATUS
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ORDER_USER_ID
import com.ucompensar.project_store.database.DataBaseHelper.Companion.TABLE_ORDER_ITEMS
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ITEM_ID
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ITEM_ORDER_ID
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ITEM_PRODUCT_ID
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ITEM_QUANTITY
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_ITEM_UNIT_PRICE
import com.ucompensar.project_store.database.DataBaseHelper.Companion.TABLE_PRODUCTS
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_PRODUCT_NAME
import com.ucompensar.project_store.database.DataBaseHelper.Companion.COLUMN_PRODUCT_IMAGE_URL

class OrderDAO(context: Context) {

    private val dbHelper = DataBaseHelper(context)
    private val db = dbHelper.writableDatabase
    private val TAG = "OrderDAO"

    fun createOrder(order: Order, items: List<OrderItem>): Long {
        db.beginTransaction()
        try {
            val orderValues = ContentValues().apply {
                put(COLUMN_ORDER_DATE, order.orderDate)
                put(COLUMN_ORDER_TOTAL, order.total)
                put(COLUMN_ORDER_STATUS, order.status)
                put(COLUMN_ORDER_USER_ID, order.userId)
            }

            val orderId = db.insert(TABLE_ORDERS, null, orderValues)

            if (orderId != -1L) {
                for (item in items) {
                    val itemValues = ContentValues().apply {
                        put(COLUMN_ITEM_ORDER_ID, orderId)
                        put(COLUMN_ITEM_PRODUCT_ID, item.productId)
                        put(COLUMN_ITEM_QUANTITY, item.quantity)
                        put(COLUMN_ITEM_UNIT_PRICE, item.unitPrice)
                    }
                    val itemId = db.insert(TABLE_ORDER_ITEMS, null, itemValues)
                    if (itemId == -1L) {
                        Log.e(TAG, "Error al insertar OrderItem para el pedido $orderId")
                        db.endTransaction()
                        return -1L
                    }
                }
                db.setTransactionSuccessful()
                return orderId
            }
            return -1L
        } catch (e: Exception) {
            Log.e(TAG, "Error durante la transacción de inserción de pedido: ${e.message}")
            return -1L
        } finally {
            db.endTransaction()
        }
    }

    fun getOrdersByUserId(userId: Int): List<Order> {
        val ordersList = mutableListOf<Order>()
        val query = "SELECT * FROM $TABLE_ORDERS WHERE $COLUMN_ORDER_USER_ID = ?"
        val cursor: Cursor? = db.rawQuery(query, arrayOf(userId.toString()))

        cursor?.use {
            while (it.moveToNext()) {
                val order = cursorToOrder(it)
                ordersList.add(order)
            }
        }
        return ordersList
    }

    fun getOrderItemsByOrderId(orderId: Long): List<OrderItem> {
        val itemsList = mutableListOf<OrderItem>()

        val query = """
            SELECT 
                oi.$COLUMN_ITEM_ID, 
                oi.$COLUMN_ITEM_ORDER_ID, 
                oi.$COLUMN_ITEM_PRODUCT_ID, 
                oi.$COLUMN_ITEM_QUANTITY, 
                oi.$COLUMN_ITEM_UNIT_PRICE,
                p.$COLUMN_PRODUCT_NAME,
                p.$COLUMN_PRODUCT_IMAGE_URL
            FROM $TABLE_ORDER_ITEMS oi
            INNER JOIN $TABLE_PRODUCTS p ON oi.$COLUMN_ITEM_PRODUCT_ID = p.${DataBaseHelper.COLUMN_PRODUCT_ID}
            WHERE oi.$COLUMN_ITEM_ORDER_ID = ?
        """.trimIndent()

        val cursor: Cursor? = db.rawQuery(query, arrayOf(orderId.toString()))

        cursor?.use {
            while (it.moveToNext()) {
                val itemId = it.getLong(it.getColumnIndexOrThrow(COLUMN_ITEM_ID))
                val orderIdFk = it.getLong(it.getColumnIndexOrThrow(COLUMN_ITEM_ORDER_ID))
                val productId = it.getInt(it.getColumnIndexOrThrow(COLUMN_ITEM_PRODUCT_ID))
                val quantity = it.getInt(it.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY))
                val unitPrice = it.getDouble(it.getColumnIndexOrThrow(COLUMN_ITEM_UNIT_PRICE))
                val productName = it.getString(it.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
                val imageUrl = it.getString(it.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_URL))

                val item = OrderItem(
                    id = itemId,
                    orderId = orderIdFk,
                    productId = productId,
                    quantity = quantity,
                    unitPrice = unitPrice
                ).apply {

                }
                itemsList.add(item)
            }
        }
        return itemsList
    }

    private fun cursorToOrder(cursor: Cursor): Order {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE))
        val total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL))
        val status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
        val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID))

        return Order(id, date, total, status, userId)
    }

    fun close() {
        dbHelper.close()
    }
}