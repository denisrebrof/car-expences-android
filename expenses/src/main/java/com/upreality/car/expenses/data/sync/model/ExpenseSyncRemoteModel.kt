package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote

data class ExpenseSyncRemoteModel(
    val remoteModel: ExpenseRemote,
    val timestamp: Long = 0L,
    val deleted: Boolean = false
)
