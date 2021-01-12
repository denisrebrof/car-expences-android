package com.upreality.car.expenses.data.model.queries

abstract class ExpenseColumnFilter(private val column: String) : IExpenseFilter {

    abstract val filter: String

    override fun getFilterExpression(): String {
        return "SELECT * FROM expenses WHERE $column LIKE $filter ORDER BY date"
    }
}

