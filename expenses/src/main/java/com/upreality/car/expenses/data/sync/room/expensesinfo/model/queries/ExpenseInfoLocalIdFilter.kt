package com.upreality.car.expenses.data.sync.room.expensesinfo.model.queries

class ExpenseInfoLocalIdFilter(
    val id: Long
) : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expense_info WHERE local_id LIKE $id"
    }
}