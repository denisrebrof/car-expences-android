package com.upreality.car.expenses.data.local.expenses.model.filters

import com.upreality.car.common.data.database.IDatabaseFilter

object ExpenseLocalEmptyFilter : IDatabaseFilter {
    override fun getFilterExpression() = "SELECT * FROM expenses"
}