package com.upreality.car.expenses.data.local.expenses.model.queries

import com.upreality.car.common.data.IDatabaseFilter

object ExpenseEmptyFilter : IDatabaseFilter {
    override fun getFilterExpression() = "SELECT * FROM expenses"
}