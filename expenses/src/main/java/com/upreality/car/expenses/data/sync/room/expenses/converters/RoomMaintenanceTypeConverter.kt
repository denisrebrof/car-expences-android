package com.upreality.car.expenses.data.sync.room.expenses.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.MaintenanceType.Maintenance
import com.upreality.car.expenses.domain.model.MaintenanceType.RepairService

class RoomMaintenanceTypeConverter {
    @TypeConverter
    fun toId(type: MaintenanceType) = when (type) {
        MaintenanceType.Undefined -> 0
        MaintenanceType.Other -> 1
        Maintenance -> 2
        RepairService -> 3
    }

    @TypeConverter
    fun fromId(id: Int) = when (id) {
        1 -> MaintenanceType.Other
        2 -> Maintenance
        3 -> RepairService
        else -> MaintenanceType.Undefined
    }
}