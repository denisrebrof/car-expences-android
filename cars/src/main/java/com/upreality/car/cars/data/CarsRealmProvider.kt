package com.upreality.car.cars.data

import io.realm.Realm
import io.realm.RealmConfiguration


object CarsRealmProvider {
    fun getRealmInstance(): Realm {
        return getRealmConfig().let(Realm::getInstance)
    }

    private fun getRealmConfig(): RealmConfiguration {
        return RealmConfiguration.Builder()
            .schemaVersion(1)
            .modules(CarRealmModule::class.java)
            .build()
    }
}