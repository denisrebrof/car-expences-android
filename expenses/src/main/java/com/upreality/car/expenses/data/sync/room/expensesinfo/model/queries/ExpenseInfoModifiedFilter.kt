package com.upreality.car.expenses.data.sync.room.expensesinfo.model.queries

import com.upreality.car.expenses.data.sync.room.expensesinfo.model.entities.ExpenseInfoSyncState

object ExpenseInfoModifiedFilter : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        val modifiedStates = arrayOf(
            ExpenseInfoSyncState.Created,
            ExpenseInfoSyncState.Updated,
            ExpenseInfoSyncState.Deleted
        )
        val sqlRange = modifiedStates.map { it.id }.joinToString(separator = "|")
        return "SELECT * FROM expense_info WHERE state LIKE $sqlRange"
    }
}