package com.upreality.car.expenses.data.sync.remote.expenses.converters

import java.util.*

object DateConverter {
    fun toDate(time: Long) = Date(time)
    fun toTime(date: Date) = date.time
}