package com.upreality.car.expenses.data.local.room.expenses.model

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import java.util.*

sealed class ExpenseRoom(val date: Date, val cost: Float) {

    companion object {
        const val DEFAULT_ID = 0L
    }

    var id = DEFAULT_ID

    class Fine(
        date: Date, cost: Float,
        val type: FinesCategories
    ) : ExpenseRoom(date, cost)


    class Maintenance(
        date: Date, cost: Float,
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseRoom(date, cost)

    class Fuel(
        date: Date, cost: Float,
        val liters: Float,
        val mileage: Float
    ) : ExpenseRoom(date, cost)
}
