package com.upreality.car.expenses.data.sync.room.expensesinfo.model.queries

object ExpenseInfoAllFilter : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expense_info"
    }
}