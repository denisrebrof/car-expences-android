package com.upreality.car.expenses.data.local.expenses.converters.room

import androidx.room.TypeConverter
import java.util.*

class RoomDateConverter {
    @TypeConverter
    fun toTimestamp(date: Date) = date.time

    @TypeConverter
    fun fromTimestamp(time: Long) = Date(time)
}