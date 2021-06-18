package com.upreality.car.auth.data.local

import com.upreality.car.auth.data.IAuthLocalDataSource
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(
    private val lastAuthStateDAO: ILastAuthStateDAO
) : IAuthLocalDataSource {

    private val authState = BehaviorProcessor.createDefault<AuthState>(AuthState.Unauthorized)

    override fun getLastAuthType(): Flowable<AuthType> = lastAuthStateDAO.get()
    override fun setLastAuthType(authType: AuthType): Completable = lastAuthStateDAO.set(authType)

    override fun getAuthState(): Flowable<AuthState> = authState
    override fun setAuthState(state: AuthState) = authState::onNext

}