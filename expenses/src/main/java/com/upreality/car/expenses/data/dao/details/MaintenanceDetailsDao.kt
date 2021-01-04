package com.upreality.car.expenses.data.dao.details

import androidx.room.Dao
import androidx.room.Query
import com.upreality.car.expenses.data.dao.BaseDao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails

@Dao
interface MaintenanceDetailsDao : BaseDao<ExpenseDetails.ExpenseMaintenanceDetails> {
    @Query("SELECT * FROM maintenance_details WHERE id = :id")
    fun get(id: Long): ExpenseDetails.ExpenseMaintenanceDetails?
}