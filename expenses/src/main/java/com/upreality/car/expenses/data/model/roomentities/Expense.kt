package com.upreality.car.expenses.data.model.roomentities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.converters.DateConverter
import com.upreality.car.expenses.data.converters.ExpenseTypeConverter
import java.util.*

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @TypeConverters(DateConverter::class)
    val date: Date,
    val cost: Float,
    @TypeConverters(ExpenseTypeConverter::class)
    val type: ExpenseType,
    val detailsId: Long
)