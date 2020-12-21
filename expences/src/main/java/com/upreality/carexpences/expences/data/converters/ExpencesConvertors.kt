package com.upreality.carexpences.expences.data.converters

import androidx.room.TypeConverter
import com.upreality.carexpences.expences.data.model.roomentities.expencedetails.FinesCategories
import com.upreality.carexpences.expences.data.model.roomentities.expencedetails.MaintanceType
import com.upreality.carexpences.expences.data.model.roomentities.ExpenceType
import java.util.*

class DateConverter {
    @TypeConverter
    fun toTimestamp(date: Date?) = date?.time

    @TypeConverter
    fun fromTimestamp(time: Long?) = time?.let { Date(it) }
}

class MaintanceTypeConverter {
    @TypeConverter
    fun toId(type: MaintanceType) = type.id

    @TypeConverter
    fun fromId(id: Int) = MaintanceType.values().firstOrNull { it.id == id }
}

class FinesTypeConverter {
    @TypeConverter
    fun toId(type: FinesCategories) = type.id

    @TypeConverter
    fun fromId(id: Int) = MaintanceType.values().firstOrNull { it.id == id }
}

class ExpenceTypeConverter {
    @TypeConverter
    fun toExpenceType(id : Int) = enumValues<ExpenceType>()[id]

    @TypeConverter
    fun fromExpenceType(type: ExpenceType) = type.ordinal
}