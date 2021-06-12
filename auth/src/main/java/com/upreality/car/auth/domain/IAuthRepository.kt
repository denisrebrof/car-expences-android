package com.upreality.car.auth.domain

import io.reactivex.Maybe

interface IAuthRepository {
    fun googleSignIn(token: String): Maybe<Account>
}