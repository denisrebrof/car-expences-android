package com.upreality.carexpences.domain.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.upreality.carexpences.data.converters.DateConverter
import com.upreality.carexpences.data.converters.MaintanceTypeConverter
import java.util.*

@Entity(tableName = "maintence_expences")
class MaintanceExpence(
    id: Long,
    @TypeConverters(DateConverter::class)
    date: Date,
    cost: Float,
    @TypeConverters(MaintanceTypeConverter::class)
    type: MaintanceType,
    mileage: Float
) : ExpenceBase(id, date, cost)