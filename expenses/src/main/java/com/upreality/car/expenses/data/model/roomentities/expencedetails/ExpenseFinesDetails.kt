package com.upreality.car.expenses.data.model.roomentities.expencedetails

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.converters.FinesTypeConverter

@Entity(tableName = "fines_details")
data class ExpenseFinesDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @TypeConverters(FinesTypeConverter::class)
    val type: FinesCategories
)
