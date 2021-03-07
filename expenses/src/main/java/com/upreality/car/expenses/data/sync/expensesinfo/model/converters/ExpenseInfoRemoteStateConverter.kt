package com.upreality.car.expenses.data.sync.expensesinfo.model.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.data.sync.expensesinfo.model.entities.ExpenseInfoSyncState

class ExpenseInfoRemoteStateConverter {
    @TypeConverter
    fun toId(state: ExpenseInfoSyncState) = state.id

    @TypeConverter
    fun fromId(id: Int) = enumValues<ExpenseInfoSyncState>().first {
        it.id == id
    }
}