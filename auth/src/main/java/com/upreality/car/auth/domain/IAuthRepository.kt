package com.upreality.car.auth.domain

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.Maybe

interface IAuthRepository {
    fun getGoogleSignInOptions(): GoogleSignInOptions
    fun googleSignIn(token: String): Maybe<Account>
    fun getSignedInState(): AuthState
}