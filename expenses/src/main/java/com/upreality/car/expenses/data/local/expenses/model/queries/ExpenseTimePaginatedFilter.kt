package com.upreality.car.expenses.data.local.expenses.model.queries

class ExpenseTimePaginatedFilter(
    cursor: Long,
    length: Int
) : ExpensePaginatedFilter(cursor, length) {
    override val orderColumn = "date"
    override val sortAscending = true
}