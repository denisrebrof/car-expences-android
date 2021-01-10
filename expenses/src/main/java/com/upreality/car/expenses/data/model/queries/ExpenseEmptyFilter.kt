package com.upreality.car.expenses.data.model.queries

object ExpenseEmptyFilter : IExpenseFilter {
    override fun getFilterExpression() = "SELECT * FROM expenses"
}