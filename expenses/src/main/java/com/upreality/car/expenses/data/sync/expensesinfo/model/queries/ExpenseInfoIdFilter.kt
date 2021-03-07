package com.upreality.car.expenses.data.sync.expensesinfo.model.queries

class ExpenseInfoIdFilter(
    val id: Long
) : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expense_info WHERE id LIKE $id"
    }
}