package com.upreality.car.auth.data.local

import android.content.SharedPreferences
import com.upreality.car.auth.data.IAuthLocalDataSource
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import data.PreferenceDelegate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(
    private val lastAuthStateDAO: ILastAuthStateDAO,
    private var accountDAO: AccountDAO
) : IAuthLocalDataSource {

    private val authState = BehaviorProcessor.createDefault<AuthState>(AuthState.Unauthorized)

    override fun getLastAuthType(): Flowable<AuthType> = lastAuthStateDAO.get()
    override fun setLastAuthType(authType: AuthType): Completable = lastAuthStateDAO.set(authType)

    override fun getAuthState(): Flowable<AuthState> = authState
    override fun setAuthState(state: AuthState) {
        authState.onNext(state)
        val account = state.let(AuthState.Authorized::class.java::cast)?.account ?: return
        account.
    }

}