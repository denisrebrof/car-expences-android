package com.upreality.car.expenses.data.model.filters

class ExpenseIdFilter(val id: Long) : ExpenseColumnFilter("id") {
    override val filter = id.toString()
}