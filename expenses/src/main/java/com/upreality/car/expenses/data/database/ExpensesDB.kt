package com.upreality.car.expenses.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.converters.DateConverter
import com.upreality.car.expenses.data.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.converters.FinesTypeConverter
import com.upreality.car.expenses.data.converters.MaintenanceTypeConverter
import com.upreality.car.expenses.data.dao.ExpensesDao
import com.upreality.car.expenses.data.model.roomentities.Expense
import com.upreality.car.expenses.data.model.roomentities.expencedetails.ExpenseFinesDetails
import com.upreality.car.expenses.data.model.roomentities.expencedetails.ExpenseFuelDetails
import com.upreality.car.expenses.data.model.roomentities.expencedetails.ExpenseMaintenanceDetails

@Database(
    entities = [Expense::class, ExpenseMaintenanceDetails::class, ExpenseFinesDetails::class, ExpenseFuelDetails::class],
    version = 1
)
abstract class ExpensesDB : RoomDatabase() {
    abstract fun getExpensesDAO(): ExpensesDao
}