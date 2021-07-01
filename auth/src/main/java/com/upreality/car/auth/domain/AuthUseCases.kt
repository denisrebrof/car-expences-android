package com.upreality.car.auth.domain

import io.reactivex.Flowable
import javax.inject.Inject

class AuthUseCases @Inject constructor(
    private val repository: IAuthRepository
) {
    fun googleSignIn(authCode: String) = repository.googleSignIn(authCode)
    fun getLastAuthType(): Flowable<AuthType> = repository.getLastAuthType()
    fun getAuthState(): Flowable<AuthState> = repository.getSignedInState()
}