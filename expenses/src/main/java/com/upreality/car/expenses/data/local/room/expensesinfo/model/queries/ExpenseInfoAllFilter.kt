package com.upreality.car.expenses.data.local.room.expensesinfo.model.queries

object ExpenseInfoAllFilter : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expense_info"
    }
}