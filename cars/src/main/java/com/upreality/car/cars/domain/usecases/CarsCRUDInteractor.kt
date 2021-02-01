package com.upreality.car.cars.domain.usecases

import com.upreality.car.cars.domain.ICarsRepository
import com.upreality.car.cars.domain.model.Car
import javax.inject.Inject

class CarsCRUDInteractor @Inject constructor(
    private val repository: ICarsRepository
) {
    fun getCarsList() = repository.getCars()

    fun getCar(carId: Long) = repository.getCar(carId)

    fun updateCar(car: Car) = repository.updateCar(car)

    fun deleteCar(car: Car) = repository.deleteCar(car)
}