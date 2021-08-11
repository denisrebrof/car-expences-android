package com.upreality.car.cars.data.converters

import com.upreality.car.brending.domain.model.CarMark
import com.upreality.car.cars.data.model.entities.CarEntity
import com.upreality.car.cars.domain.model.Car

object CarConverter {
    fun toCarEntity(car: Car): CarEntity {
        return CarEntity(
            car.id,
            car.name,
            car.mileage,
            car.mark.id
        )
    }

    fun fromCarEntity(entity: CarEntity, mark: CarMark): Car {
        return Car(
            entity.id,
            entity.name,
            entity.mileage,
            mark
        )
    }
}