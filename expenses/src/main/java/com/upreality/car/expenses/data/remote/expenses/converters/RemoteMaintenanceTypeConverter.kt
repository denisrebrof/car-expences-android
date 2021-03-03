package com.upreality.car.expenses.data.remote.expenses.converters

import com.upreality.car.expenses.domain.model.MaintenanceType

object RemoteMaintenanceTypeConverter {

    fun toMaintenanceType(typeId: Int): MaintenanceType {
        return when (typeId) {
            1 -> MaintenanceType.Maintenance
            2 -> MaintenanceType.RepairService
            3 -> MaintenanceType.Other
            else -> MaintenanceType.NotDefined
        }
    }

    fun toMaintenanceId(type: MaintenanceType): Int {
        return when (type) {
            MaintenanceType.NotDefined -> 0
            MaintenanceType.Maintenance -> 1
            MaintenanceType.RepairService -> 2
            MaintenanceType.Other -> 3
        }
    }
}