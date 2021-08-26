package com.upreality.car.expenses.data.sync.room.expenses.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomExpenseTypeConverter
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomDateConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import java.util.*

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @field:TypeConverters(RoomDateConverter::class)
    val date: Date,
    val cost: Float,
    @field:TypeConverters(RoomExpenseTypeConverter::class)
    val type: ExpenseType,
    val detailsId: Long
)