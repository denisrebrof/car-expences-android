package com.upreality.car.cars.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val mileage: Int,
    val markId: Long
)