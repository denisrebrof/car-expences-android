package com.upreality.car.cars.data.model

import com.upreality.car.cars.data.datasoures.CarsLocalDataSource
import com.upreality.car.cars.domain.ICarsRepository
import com.upreality.car.cars.domain.model.Car
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class CarsRepositoryImpl @Inject constructor(
    private val dataSource: CarsLocalDataSource
) : ICarsRepository {

    override fun getCars(): Flowable<List<Car>> {
        return dataSource.getCars()
    }

    override fun create(car: Car): Maybe<Long> {
        return dataSource.create(car)
    }

    override fun updateCar(car: Car): Completable {
        return dataSource.updateCar(car)
    }

    override fun deleteCar(car: Car): Completable {
        return dataSource.deleteCar(car)
    }
}