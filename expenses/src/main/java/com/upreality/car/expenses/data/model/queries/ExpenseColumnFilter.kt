package com.upreality.car.expenses.data.model.queries

import com.upreality.common.data.IDatabaseFilter

abstract class ExpenseColumnFilter(private val column: String) : IDatabaseFilter {

    abstract val filter: String

    override fun getFilterExpression(): String {
        return "SELECT * FROM expenses WHERE $column LIKE $filter ORDER BY date"
    }
}

