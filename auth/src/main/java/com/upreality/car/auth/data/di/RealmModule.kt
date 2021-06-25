package com.upreality.car.auth.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
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
}