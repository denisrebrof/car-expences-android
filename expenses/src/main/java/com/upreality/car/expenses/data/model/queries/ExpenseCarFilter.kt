package com.upreality.car.expenses.data.model.queries

import com.upreality.car.cars.domain.model.Car

class ExpenseCarFilter(val car: Car) : ExpenseColumnFilter("car_id") {
    override val filter = car.id.toString()
}