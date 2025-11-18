package com.ucompensar.project_store.models

data class Users(
    val id: Int? = null,
    val name: String,
    val email: String,
    val password: String?,
    val provider: String? = "local",
    val providerUserId: String? = null,
    val isAdmin: Boolean = false,
    val city: String? = null,
    val role: String? = null
)


