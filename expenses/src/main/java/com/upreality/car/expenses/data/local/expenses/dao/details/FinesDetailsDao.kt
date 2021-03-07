package com.upreality.car.expenses.data.local.expenses.dao.details

import androidx.room.Dao
import androidx.room.Query
import com.upreality.car.common.data.database.RoomBaseDao
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalDetailsEntity
import io.reactivex.Maybe

@Dao
interface FinesDetailsDao : RoomBaseDao<ExpenseLocalDetailsEntity.ExpenseFinesDetails> {
    @Query("SELECT * FROM fines_details WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseLocalDetailsEntity.ExpenseFinesDetails>
}