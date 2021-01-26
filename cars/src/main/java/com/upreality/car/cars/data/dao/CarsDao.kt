package com.upreality.car.cars.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.upreality.car.cars.data.model.CarEntity
import com.upreality.car.expenses.data.model.entities.ExpenseDetails
import com.upreality.common.data.BaseDao
import io.reactivex.Maybe

@Dao
interface CarsDao: BaseDao<CarEntity> {
    @Query("SELECT * FROM cars WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseDetails.ExpenseFinesDetails>
}