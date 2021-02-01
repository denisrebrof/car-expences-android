package com.upreality.car.expenses.data.converters

import com.upreality.car.expenses.data.model.ExpenseType
import com.upreality.car.expenses.data.model.queries.ExpenseEmptyFilter
import com.upreality.car.expenses.data.model.queries.ExpenseTypeFilter
import com.upreality.common.data.IDatabaseFilter
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