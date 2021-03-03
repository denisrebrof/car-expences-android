package com.upreality.car.expenses.data.local.expenses.model.filters

import com.upreality.car.common.data.database.IDatabaseFilter

object ExpenseEmptyFilter : IDatabaseFilter {
    override fun getFilterExpression() = "SELECT * FROM expenses"
}