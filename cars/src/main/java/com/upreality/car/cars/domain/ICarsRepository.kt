package com.upreality.car.cars.domain

import com.upreality.car.cars.domain.model.Car
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface ICarsRepository {
    fun create(car: Car): Completable
    fun updateCar(car: Car): Completable
    fun deleteCar(car: Car): Completable
    fun getCars(): Flowable<List<Car>>
    //TODO: no car case processing
    fun getCar(carId: Long): Flowable<Car>
}