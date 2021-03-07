package com.upreality.car.expenses.data.sync.expensesinfo.model.queries

import com.upreality.car.expenses.data.sync.expensesinfo.model.converters.ExpenseInfoRemoteStateConverter
import com.upreality.car.expenses.data.sync.expensesinfo.model.entities.ExpenseInfoSyncState

class ExpenseInfoStateFilter(private val state: ExpenseInfoSyncState) : IExpenseInfoFilter {

    private val converter = ExpenseInfoRemoteStateConverter()

    override fun getFilterExpression(): String {
        val stateId = converter.toId(state)
        return "SELECT * FROM expense_info WHERE state LIKE $stateId"
    }
}