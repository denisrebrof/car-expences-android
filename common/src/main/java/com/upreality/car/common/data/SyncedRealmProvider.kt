package com.upreality.car.common.data

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.mongodb.App
import io.realm.mongodb.sync.SyncConfiguration
import javax.inject.Inject

class SyncedRealmProvider @Inject constructor(
    private val app: App
) {
    fun getRealmInstance(): Realm {
        return getRealmConfig().let(Realm::getInstance)
    }

    private fun getRealmConfig(): RealmConfiguration {
        return SyncConfiguration.Builder(app.currentUser(), "partitionKey")
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
    }
}