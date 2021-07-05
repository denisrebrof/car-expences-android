package com.upreality.car.common.data

import android.util.Log
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
        return SyncConfiguration.Builder(app.currentUser(), app.currentUser()?.id)
            .errorHandler { session, error ->
                Log.e(
                    "Sync",
                    "Sync error: $error"
                )
            }
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .clientResetHandler { session, error ->
                Log.e(
                    "EXAMPLE",
                    "Client Reset required for: ${session.configuration.serverUrl} for error: $error"
                )
            }.build()
    }
}