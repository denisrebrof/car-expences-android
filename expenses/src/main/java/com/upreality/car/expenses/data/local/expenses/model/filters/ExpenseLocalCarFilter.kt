package com.upreality.car.expenses.data.local.expenses.model.filters

class ExpenseLocalCarFilter(val car_id: Long) : ExpenseLocalColumnFilter("car_id") {
    override val filter = car_id.toString()
}