package com.upreality.carexpences.expences.data.model.roomentities.expencedetails

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.carexpences.expences.data.converters.MaintanceTypeConverter

@Entity(tableName = "maintance_details")
data class ExpenceMaintanceDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @TypeConverters(MaintanceTypeConverter::class)
    val type: MaintanceType,
    val mileage: Float
)
