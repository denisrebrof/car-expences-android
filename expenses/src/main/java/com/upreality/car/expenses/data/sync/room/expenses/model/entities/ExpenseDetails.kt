package com.upreality.car.expenses.data.sync.room.expenses.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomFinesTypeConverter
import com.upreality.car.expenses.data.sync.room.expenses.converters.RoomMaintenanceTypeConverter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType

sealed class ExpenseDetails {

    @Entity(tableName = "fines_details")
    data class ExpenseFinesDetails(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        @field:TypeConverters(RoomFinesTypeConverter::class)
        val type: FinesCategories
    ) : ExpenseDetails()

    @Entity(tableName = "fuel_details")
    data class ExpenseFuelDetails(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val liters: Float,
        val mileage: Float
    ) : ExpenseDetails()

    @Entity(tableName = "maintenance_details")
    data class ExpenseMaintenanceDetails(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        @field:TypeConverters(RoomMaintenanceTypeConverter::class)
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseDetails()

}