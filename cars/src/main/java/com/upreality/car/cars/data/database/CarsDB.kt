package com.upreality.car.cars.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.car.cars.data.dao.CarEntitiesDao
import com.upreality.car.cars.data.model.entities.CarEntity

@Database(
    entities = [
        CarEntity::class
    ],
    version = 1
)
abstract class CarsDB : RoomDatabase() {
    abstract fun getCarsDAO(): CarEntitiesDao
}