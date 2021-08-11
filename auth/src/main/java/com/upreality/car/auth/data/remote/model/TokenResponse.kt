package com.upreality.car.auth.data.remote.model

data class TokenResponse(
    val access_token: String? = null,
    val refresh_token: String? = null
)
