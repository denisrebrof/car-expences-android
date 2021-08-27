package com.upreality.car.auth.data

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.upreality.car.auth.BuildConfig
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import com.upreality.car.auth.domain.IAuthRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource,
    private val localDataSource: IAuthLocalDataSource
) : IAuthRepository {

    override fun getSignedInState(): Flowable<AuthState> = remoteDataSource.getAuthState()

    override fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_SECRET)
//            .requestServerAuthCode(BuildConfig.GOOGLE_SIGN_IN_SECRET)
            .requestEmail()
            .build()
    }

    override fun googleSignIn(authCode: String): Maybe<Account> {
        return remoteDataSource.googleSignIn(authCode).doOnSuccess { account ->
            AuthState.Authorized(account).let(localDataSource::setAuthState)
            localDataSource.setLastAuthType(AuthType.GOOGLE)
        }
    }

    override fun getLastAuthType(): Flowable<AuthType> = localDataSource.getLastAuthType()

    override fun logOut(): Completable = remoteDataSource.logOut()
}