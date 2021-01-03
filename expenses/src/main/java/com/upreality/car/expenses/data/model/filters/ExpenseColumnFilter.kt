package com.upreality.car.expenses.data.model.filters

abstract class ExpenseColumnFilter(private val column: String) : IExpenseFilter {

    abstract val filter: String

    override fun getFilterExpression(): String {
        return "WHERE $column LIKE $filter"
    }
}

