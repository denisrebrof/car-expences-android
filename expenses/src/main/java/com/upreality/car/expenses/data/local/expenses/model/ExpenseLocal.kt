package com.upreality.car.expenses.data.local.expenses.model

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import java.util.*

sealed class ExpenseLocal(val date: Date, val cost: Float) {

    companion object {
        const val DEFAULT_ID = 0L
    }

    var id = DEFAULT_ID

    class Fine(
        date: Date, cost: Float,
        val type: FinesCategories
    ) : ExpenseLocal(date, cost)


    class Maintenance(
        date: Date, cost: Float,
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseLocal(date, cost)

    class Fuel(
        date: Date, cost: Float,
        val liters: Float,
        val mileage: Float
    ) : ExpenseLocal(date, cost)
}
