package com.upreality.car.cars.data.datasoures

import androidx.sqlite.db.SimpleSQLiteQuery
import com.upreality.car.brending.domain.ICarMarkRepository
import com.upreality.car.cars.data.converters.CarConverter
import com.upreality.car.cars.data.dao.CarEntitiesDao
import com.upreality.car.cars.domain.model.Car
import data.database.IDatabaseFilter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class CarsLocalDataSource @Inject constructor(
    private val carEntitiesDao: CarEntitiesDao,
    private val marksRepository: ICarMarkRepository
) {
    fun getCars(filter: IDatabaseFilter): Flowable<List<Car>> {
        val query = SimpleSQLiteQuery(filter.getFilterExpression())
        return carEntitiesDao
            .load(query)
            .map { carEntities ->
                carEntities.map { carEntity ->
                    val mark = marksRepository.getMark(carEntity.markId)
                    CarConverter.fromCarEntity(carEntity, mark)
                }
            }
    }

    fun create(car: Car): Maybe<Long> {
        val entity = CarConverter.toCarEntity(car)
        return carEntitiesDao.insert(entity)
    }

    fun updateCar(car: Car): Completable {
        val entity = CarConverter.toCarEntity(car)
        return carEntitiesDao.update(entity)
    }

    fun deleteCar(car: Car): Completable {
        val entity = CarConverter.toCarEntity(car)
        return carEntitiesDao.delete(entity)
    }
}