package com.upreality.car.expenses.data.sync.room.expensesinfo.model.entities

import androidx.room.*

@Entity(tableName = "expense_info")
data class ExpenseInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "local_id")
    val localId: Long,
    @ColumnInfo(name = "remote_id")
    val remoteId: String = String()
)
