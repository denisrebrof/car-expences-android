package com.upreality.car.expenses.data.sync.room.expenses.converters

import androidx.room.TypeConverter
import java.util.*

class RoomDateConverter {
    @TypeConverter
    fun toTimestamp(date: Date) = date.time

    @TypeConverter
    fun fromTimestamp(time: Long) = Date(time)
}