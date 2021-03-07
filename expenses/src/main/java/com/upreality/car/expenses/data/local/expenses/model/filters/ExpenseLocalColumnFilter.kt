package com.upreality.car.expenses.data.local.expenses.model.filters

import com.upreality.car.common.data.database.IDatabaseFilter

abstract class ExpenseLocalColumnFilter(private val column: String) : IDatabaseFilter {

    abstract val filter: String

    override fun getFilterExpression(): String {
        return "SELECT * FROM expenses WHERE $column LIKE $filter ORDER BY date"
    }
}

