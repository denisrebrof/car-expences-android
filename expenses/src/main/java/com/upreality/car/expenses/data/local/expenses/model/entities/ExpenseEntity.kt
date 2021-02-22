package com.upreality.car.expenses.data.local.expenses.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.local.expenses.converters.DateConverter
import com.upreality.car.expenses.data.local.expenses.converters.ExpenseTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import java.util.*

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @field:TypeConverters(DateConverter::class)
    val date: Date,
    val cost: Float,
    @field:TypeConverters(ExpenseTypeConverter::class)
    val type: ExpenseType,
    val detailsId: Long
)