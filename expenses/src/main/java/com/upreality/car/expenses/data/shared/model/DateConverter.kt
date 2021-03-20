package com.upreality.car.expenses.data.shared.model

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun toTimestamp(date: Date) = date.time

    @TypeConverter
    fun fromTimestamp(time: Long) = Date(time)
}