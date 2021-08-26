package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.data.local.TokenDAO
import com.upreality.car.auth.data.remote.api.AuthAPI
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.AuthState
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthAPI,
    private val tokenDAO: TokenDAO
) : IAuthRemoteDataSource {

    override fun googleSignIn(googleToken: String): Maybe<Account> {
        return api.googleSignIn(googleToken).map { response ->
            tokenDAO.set(response.jwtToken!!, TokenDAO.TokenType.ACCESS)
            tokenDAO.set(response.refreshToken!!, TokenDAO.TokenType.REFRESH)
            Account(id = response.id!!)
        }
    }

    override fun logOut(): Completable {
        return api.logOut()
    }

    override fun getAuthState(): Flowable<AuthState> {
        return api.getAccount().map { response ->
            Account(id = response.id!!).let(AuthState::Authorized).let(AuthState::class.java::cast)
        }.onErrorReturn {
            AuthState.Unauthorized
        }
    }
}