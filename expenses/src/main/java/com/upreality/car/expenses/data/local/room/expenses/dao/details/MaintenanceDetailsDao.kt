package com.upreality.car.expenses.data.local.room.expenses.dao.details

import androidx.room.Dao
import androidx.room.Query
import data.database.RoomBaseDao
import com.upreality.car.expenses.data.local.room.expenses.model.entities.ExpenseDetails
import io.reactivex.Maybe

@Dao
interface MaintenanceDetailsDao : RoomBaseDao<ExpenseDetails.ExpenseMaintenanceDetails> {
    @Query("SELECT * FROM maintenance_details WHERE id = :id")
    fun get(id: Long): Maybe<ExpenseDetails.ExpenseMaintenanceDetails>
}