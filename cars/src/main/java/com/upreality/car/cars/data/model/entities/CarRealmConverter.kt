package com.upreality.car.cars.data.model.entities

import com.upreality.car.brending.domain.model.CarMark
import com.upreality.car.cars.data.realm.model.CarRealm
import com.upreality.car.cars.domain.model.Car
import io.realm.Realm

object CarRealmConverter {
    fun fromDomain(car: Car, realm: Realm): CarRealm {
        return realm.createObject(CarRealm::class.java).apply {
            id = car.id
            name = car.name
            mileage = car.mileage
            markId = car.mark.id
        }
    }

    fun toDomain(dataModel: CarRealm, markProvider: (Long) -> CarMark): Car {
        return Car(
            dataModel.id,
            dataModel.name,
            dataModel.mileage,
            markProvider(dataModel.markId),
        )
    }
}