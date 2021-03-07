package com.upreality.car.expenses.data.remote.expenses.model.entities

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType

sealed class ExpenseRemoteDetailsEntity(val base_id: String) {

    data class ExpenseFinesDetails(
        val id: String,
        val type: FinesCategories
    ) : ExpenseRemoteDetailsEntity(id)

    data class ExpenseFuelDetails(
        val id: String,
        val liters: Float,
        val mileage: Float
    ) : ExpenseRemoteDetailsEntity(id)

    data class ExpenseMaintenanceDetails(
        val id: String,
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseRemoteDetailsEntity(id)

}