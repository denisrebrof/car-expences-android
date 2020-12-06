package com.upreality.carexpences.expences.model.data.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.upreality.carexpences.expences.model.data.ExpenceBase
import com.upreality.carexpences.expences.model.data.MaintanceType
import com.upreality.carexpences.expences.model.data.converters.DateConverter
import com.upreality.carexpences.expences.model.data.converters.MaintanceTypeConverter
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