package com.upreality.car.expenses.data.local.room.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.room.expenses.model.filters.ExpenseEmptyFilter
import com.upreality.car.expenses.data.local.room.expenses.model.filters.ExpenseTypeFilter
import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.data.local.room.expenses.model.filters.ExpenseTimePaginatedFilter
import com.upreality.car.expenses.domain.model.ExpenseFilter

object RoomExpenseFilterConverter {

    fun convert(filter: ExpenseFilter): IDatabaseFilter {
        return when (filter) {
            ExpenseFilter.All -> ExpenseEmptyFilter
            ExpenseFilter.Fines -> ExpenseTypeFilter(ExpenseType.Fines)
            ExpenseFilter.Maintenance -> ExpenseTypeFilter(ExpenseType.Maintenance)
            ExpenseFilter.Fuel -> ExpenseTypeFilter(ExpenseType.Fuel)
            is ExpenseFilter.Paged -> ExpenseTimePaginatedFilter(
                filter.cursor.coerceAtLeast(0),
                filter.pageSize
            )
        }
    }

}