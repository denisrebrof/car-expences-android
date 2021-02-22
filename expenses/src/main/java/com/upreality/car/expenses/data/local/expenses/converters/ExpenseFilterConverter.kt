package com.upreality.car.expenses.data.local.expenses.converters

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.data.local.expenses.model.queries.ExpenseEmptyFilter
import com.upreality.car.expenses.data.local.expenses.model.queries.ExpenseTypeFilter
import com.upreality.car.common.data.IDatabaseFilter
import com.upreality.car.expenses.domain.model.ExpenseFilter

class ExpenseFilterConverter {

    fun convert(filter: ExpenseFilter): IDatabaseFilter {
        return when (filter) {
            ExpenseFilter.All -> ExpenseEmptyFilter
            ExpenseFilter.Fines -> ExpenseTypeFilter(ExpenseType.Fines)
            ExpenseFilter.Maintenance -> ExpenseTypeFilter(ExpenseType.Maintenance)
            ExpenseFilter.Fuel -> ExpenseTypeFilter(ExpenseType.Fuel)
        }
    }

}