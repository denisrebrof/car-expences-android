package com.upreality.carexpences.expences.data.model.roomentities.expencedetails

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.carexpences.expences.data.converters.FinesTypeConverter

@Entity(tableName = "fines_details")
data class ExpenceFinesDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @TypeConverters(FinesTypeConverter::class)
    val type: FinesCategories
)
