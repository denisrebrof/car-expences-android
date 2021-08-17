package com.upreality.car.auth.domain

data class Account(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null
)
