package com.upreality.car.auth.data

import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.IAuthRepository
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource,
    private val localDataSource: IAuthLocalDataSource
) : IAuthRepository {

    override fun googleSignIn(token: String): Maybe<Account> {
        return remoteDataSource.googleSignIn(token)
    }

}