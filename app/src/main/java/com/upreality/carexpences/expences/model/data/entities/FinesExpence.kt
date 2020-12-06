package com.upreality.carexpences.expences.model.data.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.upreality.carexpences.expences.model.data.ExpenceBase
import com.upreality.carexpences.expences.model.data.FinesCategories
import com.upreality.carexpences.expences.model.data.converters.DateConverter
import com.upreality.carexpences.expences.model.data.converters.FinesTypeConverter
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