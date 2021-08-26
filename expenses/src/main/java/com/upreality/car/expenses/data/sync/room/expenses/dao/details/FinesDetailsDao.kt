package com.upreality.car.expenses.data.sync.room.expenses.dao.details

import androidx.room.Dao
import androidx.room.Query
import data.database.RoomBaseDao
import com.upreality.car.expenses.data.sync.room.expenses.model.entities.ExpenseDetails
import io.reactivex.Maybe

@Dao
interface FinesDetailsDao : RoomBaseDao<ExpenseDetails.ExpenseFinesDetails> {
    @Query("SELECT * FROM fines_details WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseDetails.ExpenseFinesDetails>
}