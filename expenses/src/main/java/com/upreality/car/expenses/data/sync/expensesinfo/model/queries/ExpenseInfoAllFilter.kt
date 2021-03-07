package com.upreality.car.expenses.data.sync.expensesinfo.model.queries

class ExpenseInfoAllFilter : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expense_info"
    }
}