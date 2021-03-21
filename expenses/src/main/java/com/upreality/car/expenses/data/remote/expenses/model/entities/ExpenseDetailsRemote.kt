package com.upreality.car.expenses.data.remote.expenses.model.entities

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType

sealed class ExpenseDetailsRemote(val base_id: String = String()) {

    data class ExpenseFinesDetails(
        val id: String = String(),
        val type: FinesCategories = FinesCategories.Other
    ) : ExpenseDetailsRemote(id)

    data class ExpenseFuelDetails(
        val id: String = String(),
        val liters: Float = 0f,
        val mileage: Float = 0f
    ) : ExpenseDetailsRemote(id)

    data class ExpenseMaintenanceDetails(
        val id: String = String(),
        val type: MaintenanceType = MaintenanceType.Other,
        val mileage: Float = 0f
    ) : ExpenseDetailsRemote(id)

}