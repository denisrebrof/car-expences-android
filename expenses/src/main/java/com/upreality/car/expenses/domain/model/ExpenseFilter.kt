package com.upreality.car.expenses.domain.model

import com.upreality.car.cars.domain.model.Car

sealed class ExpenseFilter {
    object All : ExpenseFilter()
    object Fines : ExpenseFilter()
    object Maintenance : ExpenseFilter()
    object Fuel : ExpenseFilter()
    data class CarFilter(val car: Car) : ExpenseFilter()
}