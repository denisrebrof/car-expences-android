package com.upreality.car.expenses.data.local.expensesinfo.model.queries

import com.upreality.car.expenses.data.local.expensesinfo.model.converters.ExpenseInfoRemoteStateConverter
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfoSyncState

class ExpenseInfoStateFilter(private val state: ExpenseInfoSyncState) : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        val stateId = ExpenseInfoRemoteStateConverter.toId(state)
        return "SELECT * FROM expense_info WHERE state LIKE $stateId"
    }
}