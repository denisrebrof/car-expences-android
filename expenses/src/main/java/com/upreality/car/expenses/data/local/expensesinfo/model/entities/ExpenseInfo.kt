package com.upreality.car.expenses.data.local.expensesinfo.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.local.expenses.converters.DateConverter

@Entity(tableName = "expense_info")
data class ExpenseInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val localId: Long,
    val remoteId: String,
    @field:TypeConverters(DateConverter::class)
    val state: ExpenseRemoteState = ExpenseRemoteState.Created,
    val localVersion: Long = 0,
    val remoteVersion: Long = 0
)
