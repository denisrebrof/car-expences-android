package com.upreality.car.expenses.data.dao

import androidx.room.Dao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails

@Dao
abstract class MaintenanceDetailsDao : BaseDao<ExpenseDetails.ExpenseMaintenanceDetails> {
    abstract fun get(id: Long): ExpenseDetails.ExpenseMaintenanceDetails?
}