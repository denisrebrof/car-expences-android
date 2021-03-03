package com.upreality.car.expenses.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.car.expenses.data.local.expenses.dao.ExpensesDao
import com.upreality.car.expenses.data.local.expenses.dao.details.FinesDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.FuelDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.MaintenanceDetailsDao
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseEntity
import com.upreality.car.expenses.data.local.expensesinfo.dao.ExpenseInfoDAO
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo

@Database(
    entities = [
        ExpenseEntity::class,
        ExpenseDetails.ExpenseMaintenanceDetails::class,
        ExpenseDetails.ExpenseFinesDetails::class,
        ExpenseDetails.ExpenseFuelDetails::class,
        ExpenseInfo::class,
    ],
    version = 1
)
abstract class ExpensesDB : RoomDatabase() {
    abstract fun getExpensesDAO(): ExpensesDao
    abstract fun getMaintenanceDAO(): MaintenanceDetailsDao
    abstract fun getFinesDAO(): FinesDetailsDao
    abstract fun getFuelDAO(): FuelDetailsDao
    abstract fun getExpenseInfoDAO(): ExpenseInfoDAO
}