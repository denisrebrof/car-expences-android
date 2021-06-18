package com.upreality.car.auth.data

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import com.upreality.car.auth.domain.IAuthRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource,
    private val localDataSource: IAuthLocalDataSource
) : IAuthRepository {

    private val signInState = BehaviorProcessor.createDefault<AuthState>(AuthState.Unauthorized)

    override fun getSignedInState(): Flowable<AuthState> = signInState

    override fun setAuthState(state: AuthState) {
        signInState.onNext(state)
    }

    override fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("405077401282-a1hkiv1k470atvih8rha869nue8pl9ns.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    override fun googleSignIn(token: String): Maybe<Account> {
        return remoteDataSource.googleSignIn(token).doOnSuccess { account ->
            AuthState.Authorized(account).let(localDataSource::setAuthState)
            localDataSource.setLastAuthType(AuthType.GOOGLE)
        }
    }

    override fun getLastAuthType(): Flowable<AuthType> = localDataSource.getLastAuthType()
}