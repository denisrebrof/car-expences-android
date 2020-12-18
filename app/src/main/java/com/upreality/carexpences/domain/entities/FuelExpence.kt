package com.upreality.carexpences.domain.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.upreality.carexpences.data.converters.DateConverter
import java.util.*

@Entity(tableName = "fuel_expences")
class FuelExpence(
    id: Long,
    @TypeConverters(DateConverter::class)
    date: Date,
    cost: Float,
    liters: Float,
    mileage: Float
) : ExpenceBase(id, date, cost)