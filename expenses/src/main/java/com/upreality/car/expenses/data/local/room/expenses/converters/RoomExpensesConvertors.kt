package com.upreality.car.expenses.data.local.room.expenses.converters

import androidx.room.TypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.FinesCategories.*
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.MaintenanceType.Maintenance
import com.upreality.car.expenses.domain.model.MaintenanceType.RepairService

class MaintenanceTypeConverter {
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

class FinesTypeConverter {
    @TypeConverter
    fun toId(type: FinesCategories) = when (type) {
        Parking -> 1
        RoadMarking -> 2
        SpeedLimit -> 3
        FinesCategories.Other -> 0
        FinesCategories.Undefined -> -1
    }

    @TypeConverter
    fun fromId(id: Int) = when (id) {
        -1 -> FinesCategories.Undefined
        1 -> Parking
        2 -> RoadMarking
        3 -> SpeedLimit
        else -> FinesCategories.Other
    }
}

class ExpenseTypeConverter {
    @TypeConverter
    fun toId(type: ExpenseType) = type.id

    @TypeConverter
    fun fromId(id: Int) = enumValues<ExpenseType>().first {
        it.id == id
    }
}