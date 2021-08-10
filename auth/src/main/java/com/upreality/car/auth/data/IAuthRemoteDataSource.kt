package com.upreality.car.auth.data

import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.AuthState
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IAuthRemoteDataSource {
    fun googleSignIn(googleToken: String): Maybe<Account>
    fun logOut(): Completable
    fun getAuthState(): Flowable<AuthState>
}