package com.upreality.car.expenses.data.local.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseLocalEmptyFilter
import com.upreality.car.expenses.data.local.expenses.model.filters.ExpenseLocalTypeFilter
import com.upreality.car.common.data.database.IDatabaseFilter
import com.upreality.car.expenses.domain.model.ExpenseFilter

object ExpenseLocalFilterConverter {

    fun convert(filter: ExpenseFilter): IDatabaseFilter {
        return when (filter) {
            ExpenseFilter.All -> ExpenseLocalEmptyFilter
            ExpenseFilter.Fines -> ExpenseLocalTypeFilter(ExpenseType.Fines)
            ExpenseFilter.Maintenance -> ExpenseLocalTypeFilter(ExpenseType.Maintenance)
            ExpenseFilter.Fuel -> ExpenseLocalTypeFilter(ExpenseType.Fuel)
        }
    }

}