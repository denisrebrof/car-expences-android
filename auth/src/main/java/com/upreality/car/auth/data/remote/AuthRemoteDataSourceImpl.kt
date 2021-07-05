package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.data.local.TokenDAO
import com.upreality.car.auth.data.remote.api.AuthAPI
import com.upreality.car.auth.domain.Account
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthAPI,
    private val tokenDAO: TokenDAO
) : IAuthRemoteDataSource {

    override fun googleSignIn(googleToken: String): Maybe<Account> {
        return api.googleSignIn(googleToken).map {
            tokenDAO.set(it.jwtToken!!, TokenDAO.TokenType.ACCESS)
            tokenDAO.set(it.refreshToken!!, TokenDAO.TokenType.REFRESH)
            Account(id = it.id!!)
        }
    }
}