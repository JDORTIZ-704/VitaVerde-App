package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.ucompensar.project_store.models.Product


class ProductDAO (context: Context) {

    private val dbHelper = DataBaseHelper(context)


    private fun mapCursorToProduct(cursor: Cursor): Product {

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_NAME))
        val category = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_CATEGORY))
        val price = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_PRICE))
        val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_QUANTITY))
        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_DESCRIPTION))
        val shortDescription = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRODUCT_SHORT_DESCRIPTION))
        return Product(id, name, category, price, quantity, imageUrl, description, shortDescription)
    }

    fun addProduct(product: Product): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DataBaseHelper.COLUMN_PRODUCT_CATEGORY, product.category)
            put(DataBaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DataBaseHelper.COLUMN_PRODUCT_QUANTITY, product.quantity)
            put(DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL, product.imageUrl)
            put(DataBaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.description)
            put(DataBaseHelper.COLUMN_PRODUCT_SHORT_DESCRIPTION, product.shortDescription)
        }
        val resultInsert = db.insert(DataBaseHelper.TABLE_PRODUCTS, null, values)
        db.close()
        return resultInsert
    }


    fun getProductById(productId: Int): Product? {
        val db = dbHelper.readableDatabase
        var product: Product? = null

        val cursor = db.query(
            DataBaseHelper.TABLE_PRODUCTS,
            null,
            "${DataBaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            product = mapCursorToProduct(cursor)
        }

        cursor.close()
        db.close()
        return product
    }


    fun updateProduct(product: Product): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DataBaseHelper.COLUMN_PRODUCT_CATEGORY, product.category)
            put(DataBaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DataBaseHelper.COLUMN_PRODUCT_QUANTITY, product.quantity)
            put(DataBaseHelper.COLUMN_PRODUCT_IMAGE_URL, product.imageUrl)
            put(DataBaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.description)
            put(DataBaseHelper.COLUMN_PRODUCT_SHORT_DESCRIPTION, product.shortDescription)
        }

        val rowsAffected = db.update(
            DataBaseHelper.TABLE_PRODUCTS,
            values,
            "${DataBaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(product.id.toString())
        )
        db.close()
        return rowsAffected > 0
    }



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



    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_PRODUCTS}"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                productList.add(mapCursorToProduct(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }


    fun getFiveRandomProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = dbHelper.readableDatabase

        // Usamos ORDER BY RANDOM() LIMIT 5 para obtener 5 registros aleatorios.
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_PRODUCTS} ORDER BY RANDOM() LIMIT 5"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                productList.add(mapCursorToProduct(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    fun getAllProductsSortedByName(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = dbHelper.readableDatabase


        val query = "SELECT * FROM ${DataBaseHelper.TABLE_PRODUCTS} ORDER BY ${DataBaseHelper.COLUMN_PRODUCT_NAME} ASC"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                productList.add(mapCursorToProduct(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    //************************//
    // Simular Datos Iniciales//
    //************************//

    fun checkAndSeedProducts() {
        if (getAllProducts().isEmpty()) {
            addProduct(Product(id = 0, name = "Ahuyama", category = "verdura", price = 1500.0, quantity = 30, imageUrl = "product_ahuyama", description = "Ahuyama fresca y orgánica.", shortDescription = "Verdura dulce."))
            addProduct(Product(id = 0, name = "Banano", category = "fruta", price = 2000.0, quantity = 15, imageUrl = "product_banano", description = "Banano de cosecha local.", shortDescription = "Fruta energizante."))
            addProduct(Product(id = 0, name = "Brócoli", category = "verdura", price = 3000.0, quantity = 10, imageUrl = "product_broccoli", description = "Brócoli rico en vitaminas K y C.", shortDescription = "Verdura saludable."))
            addProduct(Product(id = 0, name = "Aguacate", category = "fruta", price = 4500.0, quantity = 8, imageUrl = "product_aguacate", description = "Aguacate Hass cremoso.", shortDescription = "Ideal para ensaladas."))
            addProduct(Product(id = 0, name = "Zanahoria", category = "verdura", price = 1200.0, quantity = 50, imageUrl = "product_zanahoria", description = "Zanahorias dulces y frescas.", shortDescription = "Para jugos o cocinar."))
            addProduct(Product(id = 0, name = "Mandarina", category = "fruta", price = 2500.0, quantity = 25, imageUrl = "product_mandarina", description = "Mandarinas jugosas.", shortDescription = "Cítrico refrescante."))
            addProduct(Product(id = 0, name = "Lechuga", category = "verdura", price = 1800.0, quantity = 40, imageUrl = "product_lechuga", description = "Lechuga fresca para ensaladas.", shortDescription = "Verdura base."))
            addProduct(Product(id = 0, name = "Mango", category = "fruta", price = 3500.0, quantity = 12, imageUrl = "product_mango", description = "Mango maduro, dulce.", shortDescription = "Fruta tropical."))
        }
    }
}