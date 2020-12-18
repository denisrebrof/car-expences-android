package com.upreality.carexpences.domain.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.upreality.carexpences.data.converters.DateConverter
import com.upreality.carexpences.data.converters.FinesTypeConverter
import java.util.*

@Entity(tableName = "fines_expences")
class FinesExpence(
    id: Long,
    @TypeConverters(DateConverter::class)
    date: Date,
    cost: Float,
    @TypeConverters(FinesTypeConverter::class)
    type: FinesCategories
) : ExpenceBase(id, date, cost)