package com.upreality.car.expenses.data.model.queries

class ExpenseCarFilter(val car_id: Long) : ExpenseColumnFilter("car_id") {
    override val filter = car_id.toString()
}