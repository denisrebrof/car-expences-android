package com.upreality.car.auth.data

import com.upreality.car.auth.domain.Account
import io.reactivex.Maybe

interface IAuthRemoteDataSource {
    fun googleSignIn(googleToken: String): Maybe<Account>
}