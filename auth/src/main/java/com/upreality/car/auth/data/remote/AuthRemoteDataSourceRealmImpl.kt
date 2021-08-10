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
        return Observable.create<AuthState> { emitter ->
            val listener = object : AuthenticationListener {
                override fun loggedIn(user: User?) {
                    val userId = user?.id ?: return
                    userId.let(::Account).let(AuthState::Authorized).let(emitter::onNext)
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