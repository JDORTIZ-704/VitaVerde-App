package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import com.ucompensar.project_store.models.Product
import com.ucompensar.project_store.models.CartItem

// Nota: Asume que DataBaseHelper y sus constantes (TABLE_CART_ITEMS, COLUMN_CART_ITEM_ID, etc.) son correctas.

class CartDAO(context: Context) {
    private val dbHelper = DataBaseHelper(context)

    // Constantes de la tabla de ítems del carrito
    private val TABLE_CART = DataBaseHelper.TABLE_CART_ITEMS
    private val KEY_CART_ITEM_ID = DataBaseHelper.COLUMN_CART_ITEM_ID
    private val KEY_PRODUCT_ID = DataBaseHelper.COLUMN_CART_PRODUCT_ID
    private val KEY_QUANTITY = DataBaseHelper.COLUMN_CART_QUANTITY
    private val KEY_USER_ID = DataBaseHelper.COLUMN_CART_USER_ID


    fun addProductToCart(product: Product, userId: Int = 1): Boolean {
        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            "SELECT $KEY_CART_ITEM_ID, $KEY_QUANTITY FROM $TABLE_CART WHERE $KEY_PRODUCT_ID = ? AND $KEY_USER_ID = ?",
            arrayOf(product.id.toString(), userId.toString())
        )

        var success = false
        try {
            if (cursor.moveToFirst()) {
                // Producto ya existe, actualiza cantidad
                val currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUANTITY))
                // Se usa getLong() para el ID del carrito para evitar errores de tipo
                val cartItemId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CART_ITEM_ID))
                val newQuantity = currentQuantity + 1

                val values = ContentValues().apply {
                    put(KEY_QUANTITY, newQuantity)
                }

                val rowsAffected = db.update(
                    TABLE_CART,
                    values,
                    "$KEY_CART_ITEM_ID = ?",
                    arrayOf(cartItemId.toString())
                )
                success = rowsAffected > 0

            } else {
                // Producto no existe, inserta nuevo ítem
                val values = ContentValues().apply {
                    put(KEY_PRODUCT_ID, product.id)
                    put(KEY_QUANTITY, 1)
                    put(KEY_USER_ID, userId)
                }

                val newRowId = db.insert(TABLE_CART, null, values)
                success = newRowId != -1L
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return success
    }


    fun getCartItemsForUser(userId: Int): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT 
                C.$KEY_CART_ITEM_ID,
                C.$KEY_PRODUCT_ID, 
                C.$KEY_QUANTITY,
                P.${DataBaseHelper.COLUMN_PRODUCT_NAME},
                P.${DataBaseHelper.COLUMN_PRODUCT_PRICE},
                P.${DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL}
            FROM $TABLE_CART C
            INNER JOIN ${DataBaseHelper.TABLE_PRODUCTS} P 
                ON C.$KEY_PRODUCT_ID = P.${DataBaseHelper.COLUMN_PRODUCT_ID}
            WHERE C.$KEY_USER_ID = ?
        """

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                // Leer el ID como Long
                val cartItemId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CART_ITEM_ID))
                val productId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_ID))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUANTITY))

                // Columnas de la tabla de productos (P)
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_NAME))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_PRICE))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL))

                cartItems.add(CartItem(cartItemId, productId, name, price, quantity, imageUrl))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cartItems
    }

    fun updateItemQuantity(cartItemId: Long, newQuantity: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(KEY_QUANTITY, newQuantity)
        }

        val rowsAffected = db.update(
            TABLE_CART,
            values,
            "$KEY_CART_ITEM_ID = ?",
            arrayOf(cartItemId.toString())
        )
        db.close()
        return rowsAffected > 0
    }


    fun deleteItem(cartItemId: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(
            TABLE_CART,
            "$KEY_CART_ITEM_ID = ?",
            arrayOf(cartItemId.toString())
        )
        db.close()
        return rowsAffected > 0
    }


    fun clearCart(userId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(
            TABLE_CART,
            "$KEY_USER_ID = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return rowsAffected > 0
    }
}