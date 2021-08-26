package com.upreality.car.expenses.data.backend.model

data class ExpenseBackendModel(
    var id: Long? = null,
    var time: Long? = null,
    var cost: Float? = null,
    var typeId: Int? = null,
    var mileage: Float? = null,
    var fuelAmount: Float? = null,
    var maintenanceTypeId: Int? = null,
    var fineTypeId: Int? = null,
)
