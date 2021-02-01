package com.upreality.car.expenses.data.model.queries

import com.upreality.common.data.IDatabaseFilter

object ExpenseEmptyFilter : IDatabaseFilter {
    override fun getFilterExpression() = "SELECT * FROM expenses"
}