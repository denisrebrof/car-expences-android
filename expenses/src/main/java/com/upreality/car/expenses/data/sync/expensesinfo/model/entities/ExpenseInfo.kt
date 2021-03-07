package com.upreality.car.expenses.data.sync.expensesinfo.model.entities

import androidx.room.*
import com.upreality.car.expenses.data.sync.expensesinfo.model.converters.ExpenseInfoRemoteStateConverter
import com.upreality.car.expenses.data.local.expenses.converters.room.RoomDateConverter
import java.util.*

@Entity(tableName = "expense_info")
data class ExpenseInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "local_id")
    val localId: Long,
    @ColumnInfo(name = "timestamp")
    @field:TypeConverters(RoomDateConverter::class)
    val timestamp: Date,
    @ColumnInfo(name = "remote_id")
    val remoteId: String = String(),
    @ColumnInfo(name = "state")
    @field:TypeConverters(ExpenseInfoRemoteStateConverter::class)
    val state: ExpenseInfoSyncState = ExpenseInfoSyncState.Created
)
