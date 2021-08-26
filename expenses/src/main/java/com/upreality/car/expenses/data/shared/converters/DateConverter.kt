package com.upreality.car.expenses.data.shared.converters

import java.util.*

object DateConverter {
    fun toTimestamp(date: Date) = date.time
    fun fromTimestamp(time: Long) = Date(time)
}