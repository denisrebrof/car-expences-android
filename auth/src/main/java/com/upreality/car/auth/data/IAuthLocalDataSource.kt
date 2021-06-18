package com.upreality.car.auth.data

import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import io.reactivex.Completable
import io.reactivex.Flowable
import kotlin.reflect.KFunction1

interface IAuthLocalDataSource {
    fun getLastAuthType(): Flowable<AuthType>
    fun setLastAuthType(authType: AuthType): Completable
    fun getAuthState(): Flowable<AuthState>
    fun setAuthState(state: AuthState): KFunction1<AuthState, Unit>
}