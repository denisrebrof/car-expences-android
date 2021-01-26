package com.upreality.car.cars.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.car.cars.data.dao.CarsDao
import com.upreality.car.cars.data.model.CarEntity

@Database(
    entities = [
        CarEntity::class
    ],
    version = 1
)
abstract class CarsDB : RoomDatabase() {
    abstract fun getCarsDAO(): CarsDao
}