package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.AuthState
import io.reactivex.*
import io.realm.mongodb.App
import io.realm.mongodb.AuthenticationListener
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import javax.inject.Inject

class AuthRemoteDataSourceRealmImpl @Inject constructor(
    private val app: App
) : IAuthRemoteDataSource {

    override fun getAuthState(): Flowable<AuthState> {
        val initialState = when (val user = app.currentUser()) {
            null -> AuthState.Unauthorized
            else -> user.let(this::getUserAccount).let(AuthState::Authorized)
        }
        return getAuthObserverFlow().startWith(initialState)
    }

    private fun getUserAccount(user: User): Account {
        return Account(
            id = user.id,
            firstName = user.profile.firstName,
            lastName = user.profile.lastName,
            email = user.profile.email
        )
    }

    private fun getAuthObserverFlow(): Flowable<AuthState> {
        return Observable.create<AuthState> { emitter ->
            val listener = object : AuthenticationListener {
                override fun loggedIn(user: User?) {
                    user
                        ?.let(this@AuthRemoteDataSourceRealmImpl::getUserAccount)
                        ?.let(AuthState::Authorized)
                        ?.let(emitter::onNext)
                }

                override fun loggedOut(user: User?) {
                    AuthState.Unauthorized.let(emitter::onNext)
                }

            }.also(app::addAuthenticationListener)
            emitter.setCancellable {
                app.removeAuthenticationListener(listener)
            }
        }.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun googleSignIn(googleToken: String): Maybe<Account> {
        val credentials = Credentials.google(googleToken)
        return Maybe.fromCallable {
            app.login(credentials)
        }.map { user ->
            Account(user.id)
        }
    }

    override fun logOut(): Completable {
        return Completable.fromCallable {
            app.currentUser()?.logOut()
        }
    }
}