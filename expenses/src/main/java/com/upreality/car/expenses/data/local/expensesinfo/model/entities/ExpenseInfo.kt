package com.upreality.car.expenses.data.local.expensesinfo.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.upreality.car.expenses.data.local.expensesinfo.model.converters.ExpenseRemoteStateConverter

@Entity(tableName = "expense_info")
data class ExpenseInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "local_id")
    val localId: Long,
    @ColumnInfo(name = "remote_id")
    val remoteId: String = String(),
    @field:TypeConverters(ExpenseRemoteStateConverter::class)
    val state: ExpenseRemoteState = ExpenseRemoteState.Created,
    @ColumnInfo(name = "remote_version")
    val remoteVersion: Long = 0
)
