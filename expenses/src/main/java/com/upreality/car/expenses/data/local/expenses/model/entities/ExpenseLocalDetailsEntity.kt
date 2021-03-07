package com.upreality.car.expenses.data.local.expenses.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.local.expenses.converters.FinesTypeConverter
import com.upreality.car.expenses.data.local.expenses.converters.MaintenanceTypeConverter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.MaintenanceType

sealed class ExpenseLocalDetailsEntity {

    @Entity(tableName = "fines_details")
    data class ExpenseFinesDetails(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        @field:TypeConverters(FinesTypeConverter::class)
        val type: FinesCategories
    ) : ExpenseLocalDetailsEntity()

    @Entity(tableName = "fuel_details")
    data class ExpenseFuelDetails(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val liters: Float,
        val mileage: Float
    ) : ExpenseLocalDetailsEntity()

    @Entity(tableName = "maintenance_details")
    data class ExpenseMaintenanceDetails(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        @field:TypeConverters(MaintenanceTypeConverter::class)
        val type: MaintenanceType,
        val mileage: Float
    ) : ExpenseLocalDetailsEntity()

}