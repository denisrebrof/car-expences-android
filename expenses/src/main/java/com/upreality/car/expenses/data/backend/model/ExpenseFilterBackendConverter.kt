package com.upreality.car.expenses.data.backend.model

import com.upreality.car.expenses.domain.model.ExpenseFilter

object ExpenseFilterBackendConverter {
    fun toId(filter: ExpenseFilter): Long {
        return when (filter) {
            ExpenseFilter.All -> 0L
            is ExpenseFilter.DateRange -> 1L
            is ExpenseFilter.Id -> 2L
            is ExpenseFilter.Type -> 3L
        }
    }
}