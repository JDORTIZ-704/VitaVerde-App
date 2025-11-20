package com.ucompensar.project_store.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "usersDB"
        private const val DATABASE_VERSION = 5 // Asegúrate de incrementar la versión si ya existía para forzar onUpgrade

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
        const val COLUMN_PRODUCT_QUANTITY = "quantity"
        const val COLUMN_PRODUCT_IMAGE_URL = "image_url"

        // ------------------------------------
        // --- TABLA ORDERS (Pedidos) ---
        // ------------------------------------
        const val TABLE_ORDERS = "orders"
        const val COLUMN_ORDER_ID = "order_id"
        const val COLUMN_ORDER_DATE = "order_date"
        const val COLUMN_ORDER_TOTAL = "total"
        const val COLUMN_ORDER_STATUS = "status" // Ej: Pendiente, Procesando, Enviado, Completado
        const val COLUMN_ORDER_USER_ID = "user_id_fk" // Clave foránea al usuario que realiza el pedido

        // ------------------------------------
        // --- TABLA ORDER ITEMS (Detalles del Pedido) ---
        // ------------------------------------
        const val TABLE_ORDER_ITEMS = "order_items"
        const val COLUMN_ITEM_ID = "item_id"
        const val COLUMN_ITEM_ORDER_ID = "order_id_fk" // Clave foránea al pedido
        const val COLUMN_ITEM_PRODUCT_ID = "product_id_fk" // Clave foránea al producto
        const val COLUMN_ITEM_QUANTITY = "quantity"
        const val COLUMN_ITEM_UNIT_PRICE = "unit_price" // Precio del producto al momento de la compra


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
                $COLUMN_PRODUCT_IMAGE_URL TEXT
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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Eliminar tablas en el orden correcto (de dependiente a principal)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}