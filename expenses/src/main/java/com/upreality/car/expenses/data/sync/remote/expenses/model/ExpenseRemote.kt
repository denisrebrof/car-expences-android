package com.upreality.car.expenses.data.sync.remote.expenses.model

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import java.util.*

sealed class ExpenseRemote(val date: Date, val cost: Float) {

    companion object {
        const val DEFAULT_ID = ""
    }

    var id = DEFAULT_ID

    class Fine(
        date: Date, cost: Float,
        val type: FinesCategories
    ) : ExpenseRemote(date, cost)


    class Maintenance(
        date: Date, cost: Float,
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseRemote(date, cost)

    class Fuel(
        date: Date, cost: Float,
        val liters: Float,
        val mileage: Float
    ) : ExpenseRemote(date, cost)
}


