package com.upreality.car.expenses.data.local.expensesinfo.model.queries

import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState

object ExpenseInfoModifiedFilter : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        val modifiedStates = arrayOf(
            ExpenseInfoSyncState.Created,
            ExpenseInfoSyncState.Updated,
            ExpenseInfoSyncState.Deleted
        )
        val sqlRange = modifiedStates.map { it.id }.joinToString(prefix = "(", postfix = ")")
        return "SELECT * FROM expense_info WHERE state in $sqlRange"
    }
}