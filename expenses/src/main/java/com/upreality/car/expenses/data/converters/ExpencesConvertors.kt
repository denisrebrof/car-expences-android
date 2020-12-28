package com.upreality.car.expenses.data.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.data.model.ExpenseType
import com.upreality.car.expenses.data.model.FinesCategories
import com.upreality.car.expenses.data.model.MaintenanceType
import java.util.*

class DateConverter {
    @TypeConverter
    fun toTimestamp(date: Date) = date.time

    @TypeConverter
    fun fromTimestamp(time: Long) = Date(time)
}

class MaintenanceTypeConverter {
    @TypeConverter
    fun toId(type: MaintenanceType) = type.id

    @TypeConverter
    fun fromId(id: Int) = MaintenanceType.values().firstOrNull { it.id == id }
}

class FinesTypeConverter {
    @TypeConverter
    fun toId(type: FinesCategories) = type.id

    @TypeConverter
    fun fromId(id: Int) = FinesCategories.values().firstOrNull { it.id == id }
}

class ExpenseTypeConverter {
    @TypeConverter
    fun toExpenseType(id: Int) = enumValues<ExpenseType>()[id]

    @TypeConverter
    fun fromExpenseType(type: ExpenseType) = type.ordinal
}