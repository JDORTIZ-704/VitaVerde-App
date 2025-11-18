package com.ucompensar.project_store.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "usersDB"
        private const val DATABASE_VERSION = 4 // ⭐️ VERSIÓN INCREMENTADA A 4 ⭐️

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_PROVIDER = "provider"
        const val COLUMN_PROVIDER_ID = "provider_user_id"

        // ⭐️ NUEVAS CONSTANTES PARA ADMINISTRADOR ⭐️
        const val COLUMN_IS_ADMIN = "is_admin"
        const val COLUMN_CITY = "city"
        const val COLUMN_ROLE = "role"

        private const val CREATE_TABLE_USERS = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_PROVIDER TEXT NOT NULL DEFAULT 'local',
                $COLUMN_PROVIDER_ID TEXT,
                
                -- ⭐️ NUEVAS COLUMNAS EN LA TABLA ⭐️
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0, -- 0 (False) o 1 (True)
                $COLUMN_CITY TEXT,
                $COLUMN_ROLE TEXT
            )"""
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Al actualizar la versión, eliminamos la tabla y la creamos de nuevo
        // Esto BORRARÁ todos los datos existentes.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

}