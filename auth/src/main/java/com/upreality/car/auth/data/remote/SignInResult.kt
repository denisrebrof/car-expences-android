package com.upreality.car.auth.data.remote

sealed class SignInResult{
    object Failure: SignInResult()
    data class Success(val response: AccountResponse) : SignInResult()
}
