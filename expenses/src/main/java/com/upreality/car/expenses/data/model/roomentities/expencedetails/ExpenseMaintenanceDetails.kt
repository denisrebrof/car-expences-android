package com.upreality.car.expenses.data.model.roomentities.expencedetails

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.converters.MaintenanceTypeConverter
import com.upreality.car.expenses.data.model.MaintenanceType

@Entity(tableName = "maintenance_details")
data class ExpenseMaintenanceDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @field:TypeConverters(MaintenanceTypeConverter::class)
    val type: MaintenanceType,
    val mileage: Float
)
