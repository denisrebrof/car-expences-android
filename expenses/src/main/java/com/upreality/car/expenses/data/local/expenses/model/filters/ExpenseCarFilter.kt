package com.upreality.car.expenses.data.local.expenses.model.filters

class ExpenseCarFilter(val car_id: Long) : ExpenseColumnFilter("car_id") {
    override val filter = car_id.toString()
}