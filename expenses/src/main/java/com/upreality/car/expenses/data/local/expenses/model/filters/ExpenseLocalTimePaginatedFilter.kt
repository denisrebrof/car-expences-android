package com.upreality.car.expenses.data.local.expenses.model.filters

class ExpenseLocalTimePaginatedFilter(
    cursor: Long,
    length: Int
) : ExpenseLocalPaginatedFilter(cursor, length) {
    override val orderColumn = "date"
    override val sortAscending = true
}