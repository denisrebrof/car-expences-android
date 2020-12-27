package com.upreality.car.expenses.data.model.roomentities.expencedetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_details")
data class ExpenseFuelDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val liters: Float,
    val mileage: Float
)