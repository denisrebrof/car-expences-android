package com.upreality.car.cars.data.datasoures

import com.upreality.car.cars.data.dao.CarsDao
import com.upreality.car.cars.domain.model.Car
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class CarsLocalDataSource @Inject constructor(
    private val carsDao: CarsDao
) {
    fun getCars(): Flowable<List<Car>> {
        carsDao.
    }

    fun create(car: Car): Maybe<Long> {
        TODO("Not yet implemented")
    }

    fun updateCar(car: Car): Completable {
        TODO("Not yet implemented")
    }

    fun deleteCar(car: Car): Completable {
        TODO("Not yet implemented")
    }
}