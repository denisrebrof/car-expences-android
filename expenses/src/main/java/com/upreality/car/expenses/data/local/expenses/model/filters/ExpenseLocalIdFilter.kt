package com.upreality.car.expenses.data.local.expenses.model.filters

class ExpenseLocalIdFilter(val id: Long) : ExpenseLocalColumnFilter("id") {
    override val filter = id.toString()
}