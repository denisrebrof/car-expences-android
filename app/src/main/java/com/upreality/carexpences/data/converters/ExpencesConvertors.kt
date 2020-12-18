package com.upreality.carexpences.data.converters

import androidx.room.TypeConverter
import com.upreality.carexpences.domain.entities.FinesCategories
import com.upreality.carexpences.domain.entities.MaintanceType
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
    fun fromId(id: Int) = MaintanceType.findByValue(id)
}

class FinesTypeConverter {
    @TypeConverter
    fun toId(type: FinesCategories) = type.id

    @TypeConverter
    fun fromId(id: Int) = FinesCategories.findByValue(id)
}