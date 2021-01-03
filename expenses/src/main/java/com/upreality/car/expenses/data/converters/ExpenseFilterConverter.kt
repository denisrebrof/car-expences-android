package com.upreality.car.expenses.data.converters

import com.upreality.car.expenses.data.model.ExpenseType
import com.upreality.car.expenses.data.model.filters.ExpenseEmptyFilter
import com.upreality.car.expenses.data.model.filters.ExpenseTypeFilter
import com.upreality.car.expenses.data.model.filters.IExpenseFilter
import com.upreality.car.expenses.domain.ExpenseFilter

class ExpenseFilterConverter {

    fun convert(filter: ExpenseFilter): IExpenseFilter {
        return when (filter) {
            ExpenseFilter.All -> ExpenseEmptyFilter
            ExpenseFilter.Fines -> ExpenseTypeFilter(ExpenseType.Fines)
            ExpenseFilter.Maintenance -> ExpenseTypeFilter(ExpenseType.Maintenance)
            ExpenseFilter.Fuel -> ExpenseTypeFilter(ExpenseType.Fuel)
        }
    }

}