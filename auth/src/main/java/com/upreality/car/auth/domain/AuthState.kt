package com.upreality.car.auth.domain

sealed class AuthState {
    object Unauthorized : AuthState()
    data class Authorized(val account: Account) : AuthState()
}
