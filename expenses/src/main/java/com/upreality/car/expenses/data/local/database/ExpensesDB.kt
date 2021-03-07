package com.upreality.car.expenses.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.car.expenses.data.local.expenses.dao.ExpenseLocalEntitiesDao
import com.upreality.car.expenses.data.local.expenses.dao.details.FinesDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.FuelDetailsDao
import com.upreality.car.expenses.data.local.expenses.dao.details.MaintenanceDetailsDao
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalDetailsEntity
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseLocalEntity
import com.upreality.car.expenses.data.sync.expensesinfo.dao.ExpenseInfoDAO
import com.upreality.car.expenses.data.sync.expensesinfo.model.entities.ExpenseInfo

@Database(
    entities = [
        ExpenseLocalEntity::class,
        ExpenseLocalDetailsEntity.ExpenseMaintenanceDetails::class,
        ExpenseLocalDetailsEntity.ExpenseFinesDetails::class,
        ExpenseLocalDetailsEntity.ExpenseFuelDetails::class,
        ExpenseInfo::class,
    ],
    version = 1
)
abstract class ExpensesDB : RoomDatabase() {
    abstract fun getExpensesDAO(): ExpenseLocalEntitiesDao
    abstract fun getMaintenanceDAO(): MaintenanceDetailsDao
    abstract fun getFinesDAO(): FinesDetailsDao
    abstract fun getFuelDAO(): FuelDetailsDao
    abstract fun getExpenseInfoDAO(): ExpenseInfoDAO
}