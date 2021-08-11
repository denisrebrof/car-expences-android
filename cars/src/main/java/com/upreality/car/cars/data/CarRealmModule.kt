package com.upreality.car.cars.data

import com.upreality.car.cars.data.realm.model.CarRealm
import io.realm.annotations.RealmModule

@RealmModule(library = true, classes = [CarRealm::class])
class CarRealmModule {
}