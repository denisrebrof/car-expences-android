package com.upreality.car.expenses.data.local.expenses.model.queries

import com.upreality.car.common.data.IDatabaseFilter

abstract class ExpenseColumnFilter(private val column: String) : IDatabaseFilter {

    abstract val filter: String

    override fun getFilterExpression(): String {
        return "SELECT * FROM expenses WHERE $column LIKE $filter ORDER BY date"
    }
}

