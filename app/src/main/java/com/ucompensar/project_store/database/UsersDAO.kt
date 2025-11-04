package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import com.ucompensar.project_store.models.Users
import com.ucompensar.project_store.utils.PasswordHelper

class UsersDAO (context: Context) {

    //Instancia del DatabaseHelper -> DB

    private val dbHelper = DataBaseHelper(context)

    //función de registro

    fun registerUser(users: Users): Boolean {
        //Abrir modo escritura DB

        val db = dbHelper.writableDatabase
        //Encriptar contraseña antes de guardarla
        val passwordHashed = PasswordHelper.hashPassword(users.password)
        //contenedor Clave - Valor
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_NAME, users.name)
            put(DataBaseHelper.COLUMN_EMAIL, users.email)
            put(DataBaseHelper.COLUMN_PASSWORD, passwordHashed)
        }

        //insert los valores en la tabla
        //si retorna -1 hubo error al insertar, sino, return id

        val resultInsert = db.insert(DataBaseHelper.TABLE_USERS, null, values)
        db.close()
        return resultInsert != -1L
    }
    // Validar Login
    fun validateLogin(email: String, password: String): Users? {

        //Obtener usuario BD x email.

        val users = getUserByEmail(email)?: return false
        // verificar si el password coincide
        val passwordMatch = PasswordHelper.hashPassword(password)



    }

    // Obtener el usuario DB

    fun getUserByEmail(email: String): Users? {
        // abrir base de datos en modo lectura
        var user: Users? = null
        val dbReadUser = dbHelper.readableDatabase

        // Generar Query para obtener el usuario

        val queryGetUser = """  
            SELECT * FROM ${DataBaseHelper.TABLE_USERS}
            WHERE ${DataBaseHelper.COLUMN_EMAIL} = ?
        """
        // Ejecutar query
        val cursor = dbReadUser.rawQuery(queryGetUser, arrayOf(email))

        //recorrer cursor
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_EMAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PASSWORD))
            user = Users(id, name, email, password)


        }

        cursor.close()
        dbReadUser.close()
        return user

    }
    // Validar el email (si existe)

    fun validateEmail(email: String): Boolean {
        val dbReadUser = dbHelper.readableDatabase
        val queryGetUser = """  
            SELECT * FROM ${DataBaseHelper.TABLE_USERS}
            WHERE ${DataBaseHelper.COLUMN_EMAIL} = ?
        """
        val cursor = dbReadUser.rawQuery(queryGetUser, arrayOf(email))
        val emailExists = cursor.count > 0
        cursor.close()
        dbReadUser.close()
        return emailExists

    }
    // Eliminar usuario

    fun deleteUser(email: String): Boolean {
        val dbWriteUser = dbHelper.writableDatabase
        val rowsDeleted = dbWriteUser.delete(
            DataBaseHelper.TABLE_USERS,
            "${DataBaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        dbWriteUser.close()
        return rowsDeleted > 0
    }


    // Actualizar contraseña

    fun updateUserPassword(email: String, newPassword: String): Boolean {

        val dbWriteUser = dbHelper.writableDatabase
        val passwordHashed = PasswordHelper.hashPassword(newPassword)
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PASSWORD, passwordHashed)
        }

        val resultUpdate = dbWriteUser.update(
            DataBaseHelper.TABLE_USERS,
            values,
            "${DataBaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        dbWriteUser.close()
        return resultUpdate > 0
    }

    // Actualizar nombre

    fun updateNameUser(email: String, newName: String): Boolean {
        val dbWriteUser = dbHelper.writableDatabase
        val valuesNewName = ContentValues().apply {
            put(DataBaseHelper.COLUMN_NAME, newName)
        }

        val resultUpdate = dbWriteUser.update(
            DataBaseHelper.TABLE_USERS,
            valuesNewName,
            "${DataBaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        dbWriteUser.close()
        return resultUpdate > 0
    }

    // Actualizar email



}