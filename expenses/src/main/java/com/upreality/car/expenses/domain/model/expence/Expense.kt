package com.upreality.car.expenses.domain.model.expence

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import domain.OptionalValue
import java.util.*

sealed class Expense(open val date: Date, open val cost: Float) {

    companion object {
        const val DEFAULT_ID = 0L
    }

    var id = DEFAULT_ID

    data class Fine(
        override val date: Date,
        override val cost: Float,
        val type: FinesCategories = FinesCategories.Undefined
    ) : Expense(date, cost)


    data class Maintenance(
        override val date: Date,
        override val cost: Float,
        val type: MaintenanceType = MaintenanceType.Undefined,
        val mileage: OptionalValue<Float> = OptionalValue.Undefined
    ) : Expense(date, cost)

    data class Fuel(
        override val date: Date,
        override val cost: Float,
        val fuelAmount: OptionalValue<Float> = OptionalValue.Undefined,
        val mileage: OptionalValue<Float> = OptionalValue.Undefined
    ) : Expense(date, cost)
}