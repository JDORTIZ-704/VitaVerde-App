package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import com.ucompensar.project_store.models.Product

class ProductDAO (context: Context) {

    private val dbHelper = DataBaseHelper(context)

    //************************//
    //                        //
    // Insertar Producto      //
    //                        //
    //************************//

    fun insertProduct(product: Product): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DataBaseHelper.COLUMN_PRODUCT_CATEGORY, product.category)
            put(DataBaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DataBaseHelper.COLUMN_PRODUCT_QUANTITY, product.quantity)
            put(DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL, product.imageUrl)
        }

        val resultInsert = db.insert(DataBaseHelper.TABLE_PRODUCTS, null, values)
        db.close()
        return resultInsert != -1L
    }

    //************************//
    //                        //
    // Obtener Todos          //
    //                        //
    //************************//

    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_PRODUCTS}"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_NAME))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_CATEGORY))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_PRICE))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_QUANTITY))

                val imageUrlIdx = cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL)
                val imageUrl = if (cursor.isNull(imageUrlIdx)) null else cursor.getString(imageUrlIdx)

                val product = Product(id, name, category, price, quantity, imageUrl)
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    //************************//
    //                        //
    // Actualizar Cantidad    //
    //                        //
    //************************//

    fun updateProductQuantity(productId: Int, newQuantity: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PRODUCT_QUANTITY, newQuantity)
        }

        val rowsAffected = db.update(
            DataBaseHelper.TABLE_PRODUCTS,
            values,
            "${DataBaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
        db.close()
        return rowsAffected > 0
    }

    //************************//
    //                        //
    // Eliminar Producto      //
    //                        //
    //************************//

    fun deleteProduct(productId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DataBaseHelper.TABLE_PRODUCTS,
            "${DataBaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
        db.close()
        return rowsDeleted > 0
    }

    //************************//
    //                        //
    // Simular Datos Iniciales//
    //                        //
    //************************//

    /**
     * Inserta productos de ejemplo si la base de datos está vacía.
     */
    fun checkAndSeedProducts() {
        if (getAllProducts().isEmpty()) {
            // Producto 1: Ahuyama
            insertProduct(Product(
                name = "ahuyama",
                category = "verdura",
                price = 1500.0,
                quantity = 30,
                imageUrl = "product_ahuyama" // Usamos el nombre del drawable como referencia
            ))
            // Producto 2: Banano
            insertProduct(Product(
                name = "banano",
                category = "fruta",
                price = 2000.0,
                quantity = 15,
                imageUrl = "product_banano"
            ))
            // Producto 3: Brócoli
            insertProduct(Product(
                name = "Brócoli",
                category = "verdura",
                price = 3000.0,
                quantity = 10,
                imageUrl = "product_broccoli"
            ))
        }
    }
}
