package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.domain.Account
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthAPI
) : IAuthRemoteDataSource {

    override fun googleSignIn(googleToken: String): Maybe<Account> {
        return api.googleSignIn(googleToken).map {
            Account(id = it.id!!)
        }
    }
}