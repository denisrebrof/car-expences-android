package com.upreality.car.expenses.data.remote.expenses.model

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType
import java.util.*

sealed class ExpenseFirestore(val date: Date, val cost: Float) {

    companion object {
        const val DEFAULT_ID = ""
    }

    var id = DEFAULT_ID

    class Fine(
        date: Date, cost: Float,
        val type: FinesCategories
    ) : ExpenseFirestore(date, cost)


    class Maintenance(
        date: Date, cost: Float,
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseFirestore(date, cost)

    class Fuel(
        date: Date, cost: Float,
        val liters: Float,
        val mileage: Float
    ) : ExpenseFirestore(date, cost)
}


