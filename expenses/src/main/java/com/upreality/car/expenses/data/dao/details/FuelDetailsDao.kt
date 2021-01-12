package com.upreality.car.expenses.data.dao.details

import androidx.room.Dao
import androidx.room.Query
import com.upreality.car.expenses.data.dao.BaseDao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails
import io.reactivex.Flowable

@Dao
interface FuelDetailsDao : BaseDao<ExpenseDetails.ExpenseFuelDetails> {
    @Query("SELECT * FROM fuel_details WHERE id = :id")
    fun get(id: Long): Flowable<ExpenseDetails.ExpenseFuelDetails>
}