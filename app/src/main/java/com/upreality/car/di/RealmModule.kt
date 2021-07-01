package com.upreality.car.di

import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.AuthenticationListener
import io.realm.mongodb.User
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
        val app = App(config)
        app.addAuthenticationListener(object : AuthenticationListener{
            override fun loggedIn(user: User?) {
                Log.d("","Logged in")
            }

            override fun loggedOut(user: User?) {
                Log.d("","Logged out")
            }
        })
        return app
    }

    @Provides
    @Singleton
    @Named("Sync")
    fun getSyncedRealmApp(
        app: App
    ): Realm {
        val config = SyncConfiguration.Builder(app.currentUser(), app.currentUser()?.id)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        return Realm.getInstance(config)
    }
}