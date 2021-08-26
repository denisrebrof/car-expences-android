package com.upreality.car.expenses.data.sync.room.expensesinfo.model.queries

class ExpenseInfoRemoteIdFilter(
    val id: String
) : IExpenseInfoFilter {
    override fun getFilterExpression(): String {
        return "SELECT * FROM expense_info WHERE remote_id = '$id'"
    }
}