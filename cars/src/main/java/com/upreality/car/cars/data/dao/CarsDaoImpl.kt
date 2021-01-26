package com.upreality.car.cars.data.dao

import com.upreality.car.cars.data.model.CarEntity
import com.upreality.car.expenses.data.model.entities.ExpenseEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

class CarsDaoImpl : CarsDao {
    override fun insert(car: CarEntity): Maybe<Long> {
        TODO("Not yet implemented")
    }

    override fun update(car: CarEntity): Completable {
        TODO("Not yet implemented")
    }

    override fun delete(car: CarEntity): Completable {
        TODO("Not yet implemented")
    }

    override fun get(): Flowable<List<ExpenseEntity>> {
        TODO("Not yet implemented")
    }
}