package com.upreality.car.expenses.data.model.filters

object ExpenseEmptyFilter : IExpenseFilter {
    override fun getFilterExpression() = String()
}