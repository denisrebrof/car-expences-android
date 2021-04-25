package com.upreality.car.expenses.data.local.expensesinfo.model.entities

import androidx.room.*
import com.upreality.car.expenses.data.local.expensesinfo.model.converters.ExpenseInfoRemoteStateConverter
import com.upreality.car.expenses.data.shared.model.DateConverter
import java.util.*

@Entity(tableName = "expense_info")
data class ExpenseInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "local_id")
    val localId: Long,
    @ColumnInfo(name = "remote_id")
    val remoteId: String = String()
)
