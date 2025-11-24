package com.ucompensar.project_store.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "usersDB"
        private const val DATABASE_VERSION = 7

        // ------------------------------------
        // --- TABLA USERS ---
        // ------------------------------------
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_PROVIDER = "provider"
        const val COLUMN_PROVIDER_ID = "provider_user_id"
        const val COLUMN_IS_ADMIN = "is_admin"
        const val COLUMN_CITY = "city"
        const val COLUMN_ROLE = "role"

        // ------------------------------------
        // --- TABLA PRODUCTS ---
        // ------------------------------------
        const val TABLE_PRODUCTS = "products"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_CATEGORY = "category"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_QUANTITY = "quantity" // Stock disponible
        const val COLUMN_PRODUCT_IMAGE_URL = "image_url"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
        const val COLUMN_PRODUCT_SHORT_DESCRIPTION = "short_description"

        // ------------------------------------
        // --- TABLA ORDERS (Pedidos) ---
        // ------------------------------------
        const val TABLE_ORDERS = "orders"
        const val COLUMN_ORDER_ID = "order_id"
        const val COLUMN_ORDER_DATE = "order_date"
        const val COLUMN_ORDER_TOTAL = "total"
        const val COLUMN_ORDER_STATUS = "status"
        const val COLUMN_ORDER_USER_ID = "user_id_fk"

        // ------------------------------------
        // --- TABLA ORDER ITEMS (Detalles del Pedido) ---
        // ------------------------------------
        const val TABLE_ORDER_ITEMS = "order_items"
        const val COLUMN_ITEM_ID = "item_id"
        const val COLUMN_ITEM_ORDER_ID = "order_id_fk"
        const val COLUMN_ITEM_PRODUCT_ID = "product_id_fk"
        const val COLUMN_ITEM_QUANTITY = "quantity"
        const val COLUMN_ITEM_UNIT_PRICE = "unit_price"
        // ------------------------------------
        // --- NUEVA TABLA CART ITEMS (Carrito de Compras) ---
        // ------------------------------------
        const val TABLE_CART_ITEMS = "cart_items"
        const val COLUMN_CART_ITEM_ID = "cart_item_id"
        const val COLUMN_CART_PRODUCT_ID = "product_id_fk"
        const val COLUMN_CART_QUANTITY = "quantity"
        const val COLUMN_CART_USER_ID = "user_id_fk"

        // --- SENTENCIAS SQL DE CREACIÓN ---

        private const val CREATE_TABLE_USERS = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_PROVIDER TEXT NOT NULL DEFAULT 'local',
                $COLUMN_PROVIDER_ID TEXT,
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0,
                $COLUMN_CITY TEXT,
                $COLUMN_ROLE TEXT
            )"""


        private const val CREATE_TABLE_PRODUCTS = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                $COLUMN_PRODUCT_PRICE REAL NOT NULL, 
                $COLUMN_PRODUCT_QUANTITY INTEGER NOT NULL,
                $COLUMN_PRODUCT_IMAGE_URL TEXT,
                $COLUMN_PRODUCT_DESCRIPTION TEXT, 
                $COLUMN_PRODUCT_SHORT_DESCRIPTION TEXT
            )"""

        private const val CREATE_TABLE_ORDERS = """
            CREATE TABLE $TABLE_ORDERS (
                $COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_DATE TEXT NOT NULL,
                $COLUMN_ORDER_TOTAL REAL NOT NULL,
                $COLUMN_ORDER_STATUS TEXT NOT NULL,
                $COLUMN_ORDER_USER_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_ORDER_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )"""

        private const val CREATE_TABLE_ORDER_ITEMS = """
            CREATE TABLE $TABLE_ORDER_ITEMS (
                $COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ITEM_ORDER_ID INTEGER NOT NULL,
                $COLUMN_ITEM_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_ITEM_QUANTITY INTEGER NOT NULL,
                $COLUMN_ITEM_UNIT_PRICE REAL NOT NULL,
                FOREIGN KEY($COLUMN_ITEM_ORDER_ID) REFERENCES $TABLE_ORDERS($COLUMN_ORDER_ID),
                FOREIGN KEY($COLUMN_ITEM_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID)
            )"""

        private const val CREATE_TABLE_CART_ITEMS = """
            CREATE TABLE $TABLE_CART_ITEMS (
                $COLUMN_CART_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CART_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_CART_QUANTITY INTEGER NOT NULL,
                $COLUMN_CART_USER_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_CART_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID),
                FOREIGN KEY($COLUMN_CART_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                UNIQUE ($COLUMN_CART_PRODUCT_ID, $COLUMN_CART_USER_ID) ON CONFLICT REPLACE
            )"""
    }

    // ------------------------------------
    // --- MÉTODOS DE LA BASE DE DATOS ---
    // ------------------------------------

    override fun onCreate(db: SQLiteDatabase) {
        // Crear las tablas en el orden correcto
        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_PRODUCTS)
        db.execSQL(CREATE_TABLE_ORDERS)
        db.execSQL(CREATE_TABLE_ORDER_ITEMS)
        db.execSQL(CREATE_TABLE_CART_ITEMS) // Crear la nueva tabla del carrito
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Si la versión actual es menor a 7, crea la nueva tabla sin borrar los datos existentes
        if (oldVersion < 7) {
            // Asegurarse de que las tablas anteriores se recreen (si no existen) o se mantengan.
            // Para la nueva versión 7, solo añadiremos la tabla del carrito
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")

            // Y luego crear todas las tablas, incluyendo la nueva de carrito
            onCreate(db)
        } else {
            // Manejo de otras versiones si es necesario.
            // Por defecto, se eliminan y se vuelven a crear todas las tablas si la versión cambia.
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CART_ITEMS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }
    }
}