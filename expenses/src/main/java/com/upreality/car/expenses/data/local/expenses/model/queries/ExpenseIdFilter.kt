package com.upreality.car.expenses.data.local.expenses.model.queries

class ExpenseIdFilter(val id: Long) : ExpenseColumnFilter("id") {
    override val filter = id.toString()
}