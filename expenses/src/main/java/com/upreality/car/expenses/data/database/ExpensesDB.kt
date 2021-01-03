package com.upreality.car.expenses.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upreality.car.expenses.data.dao.ExpensesDao
import com.upreality.car.expenses.data.dao.FinesDetailsDao
import com.upreality.car.expenses.data.dao.FuelDetailsDao
import com.upreality.car.expenses.data.dao.MaintenanceDetailsDao
import com.upreality.car.expenses.data.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.model.entities.ExpenseEntity

@Database(
    entities = [
        ExpenseEntity::class,
        ExpenseDetails.ExpenseMaintenanceDetails::class,
        ExpenseDetails.ExpenseFinesDetails::class,
        ExpenseDetails.ExpenseFuelDetails::class
    ],
    version = 1
)
abstract class ExpensesDB : RoomDatabase() {
    abstract fun getExpensesDAO(): ExpensesDao
    abstract fun getMaintenanceDAO(): MaintenanceDetailsDao
    abstract fun getFinesDAO(): FinesDetailsDao
    abstract fun getFuelDAO(): FuelDetailsDao
}