package com.upreality.car.expenses.data.local.expenses.dao.details

import androidx.room.Dao
import androidx.room.Query
import com.upreality.car.common.data.RoomBaseDao
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseDetails
import io.reactivex.Maybe

@Dao
interface FuelDetailsDao : RoomBaseDao<ExpenseDetails.ExpenseFuelDetails> {
    @Query("SELECT * FROM fuel_details WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseDetails.ExpenseFuelDetails>
}