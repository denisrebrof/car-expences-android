package com.upreality.car.expenses.data.realm.model

import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType

object FineTypeConverter {
    fun toId(type: FinesCategories) = when (type) {
        FinesCategories.Parking -> 1
        FinesCategories.RoadMarking -> 2
        FinesCategories.SpeedLimit -> 3
        FinesCategories.Other -> 0
    }

    fun fromId(id: Int) = when (id) {
        1 -> FinesCategories.Parking
        2 -> FinesCategories.RoadMarking
        3 -> FinesCategories.SpeedLimit
        else -> FinesCategories.Other
    }
}

object MaintenanceTypeConverter {
    fun toId(type: MaintenanceType) = when (type) {
        MaintenanceType.NotDefined -> 0
        MaintenanceType.Other -> 1
        MaintenanceType.Maintenance -> 2
        MaintenanceType.RepairService -> 3
    }

    fun fromId(id: Int) = when (id) {
        1 -> MaintenanceType.Other
        2 -> MaintenanceType.Maintenance
        3 -> MaintenanceType.RepairService
        else -> MaintenanceType.NotDefined
    }
}