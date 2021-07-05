package com.upreality.car.cars.data.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CarRealm : RealmObject() {
    @PrimaryKey
    var id: Long = 0L
    var name: String = String()
    var mileage: Int = 0
    var markId: Long = 0L
}