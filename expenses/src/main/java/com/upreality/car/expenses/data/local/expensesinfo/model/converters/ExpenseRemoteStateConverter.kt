package com.upreality.car.expenses.data.local.expensesinfo.model.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseRemoteState

class ExpenseRemoteStateConverter {
    @TypeConverter
    fun toId(state: ExpenseRemoteState) = state.id

    @TypeConverter
    fun fromId(id: Int) = enumValues<ExpenseRemoteState>().first {
        it.id == id
    }
}