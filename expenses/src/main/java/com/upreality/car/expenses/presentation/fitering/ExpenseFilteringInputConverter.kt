package com.upreality.car.expenses.presentation.fitering

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import presentation.ValidationResult
import java.util.*
import javax.inject.Inject

class ExpenseFilteringInputConverter @Inject constructor() {
    fun toFiltersList(
        dateRangeState: ValidationResult<DateRangeSelection, DateRange>,
        typeState: ValidationResult<ExpenseFilteringTypeMask, Set<ExpenseType>>
    ): Result<List<ExpenseFilter>> = kotlin.runCatching {
        val dateRange = dateRangeState.requireValid()
        val assignedTypes = typeState.requireValid().toList()

        val typesList = assignedTypes.map { expenseType ->
            when (expenseType) {
                ExpenseType.Fines -> Expense.Fine::class
                ExpenseType.Fuel -> Expense.Fuel::class
                ExpenseType.Maintenance -> Expense.Maintenance::class
            }
        }

        return@runCatching listOf(
            ExpenseFilter.DateRange(dateRange.startDate, dateRange.endDate),
            ExpenseFilter.Type(typesList)
        )
    }
}