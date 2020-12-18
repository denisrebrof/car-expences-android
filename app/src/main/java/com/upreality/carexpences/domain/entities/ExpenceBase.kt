package com.upreality.carexpences.domain.entities

import androidx.room.PrimaryKey
import java.util.*

open class ExpenceBase(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Date,
    val cost: Float
)