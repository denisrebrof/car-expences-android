package com.upreality.carexpences.expences.model.data.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.upreality.carexpences.expences.model.data.ExpenceBase
import com.upreality.carexpences.expences.model.data.converters.DateConverter
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