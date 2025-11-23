package com.ucompensar.project_store.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.ucompensar.project_store.models.Users
import com.ucompensar.project_store.utils.PasswordHelper

class UsersDAO (context: Context) {

    // Instance of DatabaseHelper -> DB-USERS

    private val dbHelper = DataBaseHelper(context)

    //************************//
    //                        //
    // Registration function  //
    //                        //
    //************************//

    /*
    * 1. Open the database in write mode
    * 2. Encrypt password
    * 3. Statement of updated values
    */

    fun registerUser (users: Users): Boolean {
        val db = dbHelper.writableDatabase
        val passwordHashed = PasswordHelper.hashPassword(users.password ?: return false)
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_NAME, users.name)
            put(DataBaseHelper.COLUMN_EMAIL, users.email)
            put(DataBaseHelper.COLUMN_PASSWORD, passwordHashed)
            put(DataBaseHelper.COLUMN_PROVIDER, users.provider ?: "local")
            put(DataBaseHelper.COLUMN_PROVIDER_ID, users.providerUserId)


            put(DataBaseHelper.COLUMN_IS_ADMIN, if (users.isAdmin) 1 else 0)
            put(DataBaseHelper.COLUMN_CITY, users.city)
            put(DataBaseHelper.COLUMN_ROLE, users.role)
        }

        // Insert values into the table
        // Return -1 if there is an error

        val resultInsert = db.insert(DataBaseHelper.TABLE_USERS, null, values)
        db.close()
        return resultInsert != -1L
    }

    //************************//
    //                        //
    // Registration Google    //
    //                        //
    //************************//

    fun registerGoogleUser (users: Users): Boolean {
        val db = dbHelper.writableDatabase

        val cursor: Cursor = db.query(
            DataBaseHelper.TABLE_USERS,
            arrayOf(DataBaseHelper.COLUMN_EMAIL),
            "${DataBaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(users.email),
            null, null, null
        )

        val exists = cursor.moveToFirst()
        cursor.close()

        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_NAME, users.name)
            put(DataBaseHelper.COLUMN_EMAIL, users.email)
            put(DataBaseHelper.COLUMN_PROVIDER, "google")
            put(DataBaseHelper.COLUMN_PROVIDER_ID, users.providerUserId)
            putNull(DataBaseHelper.COLUMN_PASSWORD)
            put(DataBaseHelper.COLUMN_IS_ADMIN, if (users.isAdmin) 1 else 0)
            put(DataBaseHelper.COLUMN_CITY, users.city)
            put(DataBaseHelper.COLUMN_ROLE, users.role)
        }

        if (exists) {
            db.update(
                DataBaseHelper.TABLE_USERS,
                values,
                "${DataBaseHelper.COLUMN_EMAIL} = ?",
                arrayOf(users.email)
            )
        } else {
            db.insert(DataBaseHelper.TABLE_USERS, null, values)
        }
        db.close()
        return true
    }


    //************************//
    //                        //
    // Login validation       //
    //                        //
    //************************//

    fun validateLogin (email: String, password: String): Boolean {



        val user = getUserByEmail(email) ?: return false

        if (user.provider == "google" || user.password.isNullOrEmpty()) {
            return false
        }

        return PasswordHelper.verificationPassword(password, user.password)
    }

    // Function to retrieve users from the database

    fun getUserByEmail (email: String): Users? {
        val dbReadUser = dbHelper.readableDatabase
        var user: Users? = null



        val queryGetUser = """  
            SELECT * FROM ${DataBaseHelper.TABLE_USERS}
            WHERE ${DataBaseHelper.COLUMN_EMAIL} = ?
        """

        val cursor = dbReadUser.rawQuery(queryGetUser, arrayOf(email))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME))
            val emailDb = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_EMAIL))

            val passIdx = cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PASSWORD)
            val password = if (cursor.isNull(passIdx)) null else cursor.getString(passIdx)


            val providerIdx = cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROVIDER)
            val provider = if (cursor.isNull(providerIdx)) null else cursor.getString(providerIdx)

            val providerIdIdx = cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROVIDER_ID)
            val providerUserId = if (cursor.isNull(providerIdIdx)) null else cursor.getString(providerIdIdx)

            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_IS_ADMIN)) == 1

            val cityIdx = cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_CITY)
            val city = if (cursor.isNull(cityIdx)) null else cursor.getString(cityIdx)

            val roleIdx = cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ROLE)
            val role = if (cursor.isNull(roleIdx)) null else cursor.getString(roleIdx)


            user = Users(
                id = id,
                name = name,
                email = emailDb,
                password = password,
                provider = provider,
                providerUserId = providerUserId,

                // Asignar los nuevos campos
                isAdmin = isAdmin,
                city = city,
                role = role
            )
        }

        cursor.close()
        dbReadUser.close()
        return user
    }

    //***********************************************************//
    //                                                           //
    // Validation - If the email already exists in the database  //
    //                                                           //
    //***********************************************************//



    fun validateEmail (email: String): Boolean {
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

    //************************//
    //                        //
    // Delete user            //
    //                        //
    //************************//



    fun deleteUser (email: String): Boolean {
        val dbWriteUser = dbHelper.writableDatabase
        val rowsDeleted = dbWriteUser.delete(
            DataBaseHelper.TABLE_USERS,
            "${DataBaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        dbWriteUser.close()
        return rowsDeleted > 0
    }

    //************************//
    //                        //
    // Update password        //
    //                        //
    //************************//


    fun updateUserPassword (email: String, newPassword: String): Boolean {

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

    //************************//
    //                        //
    // Update name            //
    //                        //
    //************************//


    fun updateNameUser (email: String, newName: String): Boolean {
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

    //************************//
    //                        //
    // Update Email           //
    //                        //
    //************************//


    fun updateEmail (email: String, newEmail: String): Boolean {

        // Check if the new email already exists in the database
        // If it does, return false

        val existingUser = getUserByEmail(newEmail)
        if (existingUser != null) {
            return false
        }

        val dbWriteUser = dbHelper.writableDatabase
        val valuesNewEmail = ContentValues().apply {
            put(DataBaseHelper.COLUMN_EMAIL, newEmail)
        }

        val resultUpdate = dbWriteUser.update(
            DataBaseHelper.TABLE_USERS,
            valuesNewEmail,
            "${DataBaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        dbWriteUser.close()
        return resultUpdate > 0
    }
}