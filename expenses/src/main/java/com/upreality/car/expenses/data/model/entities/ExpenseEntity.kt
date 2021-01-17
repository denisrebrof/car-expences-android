package com.upreality.car.expenses.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.converters.DateConverter
import com.upreality.car.expenses.data.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.model.ExpenseType
import java.util.*

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val carId: Long,
    @field:TypeConverters(DateConverter::class)
    val date: Date,
    val cost: Float,
    @field:TypeConverters(ExpenseTypeConverter::class)
    val type: ExpenseType,
    val detailsId: Long
)