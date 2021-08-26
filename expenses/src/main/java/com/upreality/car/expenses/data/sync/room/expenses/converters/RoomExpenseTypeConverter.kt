package com.upreality.car.expenses.data.sync.room.expenses.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType

class RoomExpenseTypeConverter {
    @TypeConverter
    fun toId(type: ExpenseType) = type.id

    @TypeConverter
    fun fromId(id: Int) = enumValues<ExpenseType>().first {
        it.id == id
    }
}