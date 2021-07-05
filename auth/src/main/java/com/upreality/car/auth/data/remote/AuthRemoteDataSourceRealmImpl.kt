package com.upreality.car.auth.data.remote

import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.domain.Account
import io.reactivex.Maybe
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import javax.inject.Inject

class AuthRemoteDataSourceRealmImpl @Inject constructor(
    private val app: App
): IAuthRemoteDataSource {
    override fun googleSignIn(googleToken: String): Maybe<Account> {
        val credentials = Credentials.google(googleToken)
        return Maybe.fromCallable {
            app.login(credentials)
        }.map { user ->
            Account(user.id)
        }
    }
}