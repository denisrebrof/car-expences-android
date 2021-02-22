package com.upreality.car.expenses.data.local.expensesinfo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_info")
data class ExpenseInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val localId: Long,
    val remoteId: String,
    val localVersion: Long = 0,
    val remoteVersion: Long = 0
)
