package com.upreality.car.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.sync.SyncConfiguration
import javax.inject.Named
import javax.inject.Singleton

const val realmAppId = "carexpenses-jfgls"

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun getRealmApp(
        @ApplicationContext
        context: Context
    ): App {
        Realm.init(context)
        val config = AppConfiguration
            .Builder(realmAppId)
            .build()
        return App(config)
    }

    @Provides
    @Singleton
    @Named("Sync")
    fun getSyncedRealmApp(
        app: App
    ): Realm {
        val config = SyncConfiguration.Builder(app.currentUser(), "partitionKey")
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        return Realm.getInstance(config)
    }
}