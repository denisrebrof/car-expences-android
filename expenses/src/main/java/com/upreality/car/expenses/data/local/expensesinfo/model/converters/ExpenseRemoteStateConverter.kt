package com.upreality.car.expenses.data.local.expensesinfo.model.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState

class ExpenseRemoteStateConverter {
    @TypeConverter
    fun toId(state: ExpenseInfoSyncState) = state.id

    @TypeConverter
    fun fromId(id: Int) = enumValues<ExpenseInfoSyncState>().first {
        it.id == id
    }
}