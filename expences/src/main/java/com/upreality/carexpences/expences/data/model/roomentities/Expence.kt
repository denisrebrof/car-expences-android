package com.upreality.carexpences.expences.data.model.roomentities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.carexpences.expences.data.converters.*
import java.util.*
@Entity(tableName = "expences")
data class Expence(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @TypeConverters(DateConverter::class)
    val date: Date,
    val cost: Float,
    @TypeConverters(ExpenceTypeConverter::class)
    val type: ExpenceType,
    val detailsId: Long
)