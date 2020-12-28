package com.upreality.car.expenses.data.model

enum class MaintenanceType(val id : Int) {
    NotDefined(-1),
    Maintenance(0),
    RepairService(1),
    Other(2),
}