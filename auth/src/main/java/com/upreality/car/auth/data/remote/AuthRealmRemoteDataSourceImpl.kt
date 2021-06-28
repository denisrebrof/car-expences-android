package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.data.remote.model.RealmUserConverter
import com.upreality.car.auth.domain.Account
import io.reactivex.Maybe
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import javax.inject.Inject

class AuthRealmRemoteDataSourceImpl @Inject constructor(
    private val app: App
) : IAuthRemoteDataSource {

    override fun googleSignIn(googleToken: String): Maybe<Account> {
        val credentials = Credentials.google(googleToken)
        return Maybe.create { emitter ->
            app.loginAsync(credentials) { loginResult ->
                when {
                    loginResult.isSuccess -> loginResult.get()
                        .let(RealmUserConverter::from)
                        .let(emitter::onSuccess)
                    else -> emitter.onError(loginResult.error)
                }
            }
        }
    }
}