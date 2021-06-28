package com.upreality.car.auth.data.remote.model

import com.upreality.car.auth.domain.Account
import io.realm.mongodb.User

object RealmUserConverter {
    fun from(user: User): Account {
        return Account(
            id = user.id
        )
    }
}