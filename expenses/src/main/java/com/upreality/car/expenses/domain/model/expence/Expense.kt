package com.upreality.car.expenses.domain.model.expence

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import java.util.*

sealed class Expense(open val date: Date, open val cost: Float) {

    companion object {
        const val DEFAULT_ID = 0L
    }

    var id = DEFAULT_ID

    data class Fine(
        override val date: Date,
        override val cost: Float,
        val type: FinesCategories
    ) : Expense(date, cost)


    data class Maintenance(
        override val date: Date,
        override val cost: Float,
        val type: MaintenanceType,
        val mileage: Float
    ) : Expense(date, cost)

    data class Fuel(
        override val date: Date,
        override val cost: Float,
        val liters: Float,
        val mileage: Float
    ) : Expense(date, cost)
}