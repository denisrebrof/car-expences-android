package com.upreality.car.expenses.data.shared.converters

import com.upreality.car.expenses.domain.model.MaintenanceType

object MaintenanceTypeConverter {
    fun toId(type: MaintenanceType) = when (type) {
        MaintenanceType.Undefined -> 0
        MaintenanceType.Other -> 1
        MaintenanceType.Maintenance -> 2
        MaintenanceType.RepairService -> 3
    }

    fun fromId(id: Int) = when (id) {
        1 -> MaintenanceType.Other
        2 -> MaintenanceType.Maintenance
        3 -> MaintenanceType.RepairService
        else -> MaintenanceType.Undefined
    }
}