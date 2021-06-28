package com.upreality.car.auth.domain

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IAuthRepository {
    fun getGoogleSignInOptions(): GoogleSignInOptions
    fun googleSignIn(authCode: String): Maybe<Account>
    fun getSignedInState(): Flowable<AuthState>
    fun setAuthState(state: AuthState)
    fun getLastAuthType(): Flowable<AuthType>
}