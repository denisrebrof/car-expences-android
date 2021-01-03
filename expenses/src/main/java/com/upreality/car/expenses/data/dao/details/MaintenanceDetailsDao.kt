package com.upreality.car.expenses.data.dao.details

import androidx.room.Dao
import com.upreality.car.expenses.data.dao.BaseDao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails

@Dao
interface MaintenanceDetailsDao : BaseDao<ExpenseDetails.ExpenseMaintenanceDetails> {
    fun get(id: Long): ExpenseDetails.ExpenseMaintenanceDetails?
}