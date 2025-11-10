package com.ucompensar.project_store.utils

import java.security.MessageDigest

object PasswordHelper {
    fun hashPassword (password: String): String {
        val bytesPassword = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytesPassword)

        return digest.fold("") { str, it -> str + "%02x".format(it) }

    }

    fun verificationPassword (password: String, passwordHashed: String): Boolean {
        return hashPassword(password) == passwordHashed
    }
}
