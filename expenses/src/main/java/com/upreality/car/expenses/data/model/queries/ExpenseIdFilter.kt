package com.upreality.car.expenses.data.model.queries

class ExpenseIdFilter(val id: Long) : ExpenseColumnFilter("id") {
    override val filter = id.toString()
}