package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote

sealed class ExpenseSyncRemoteModel(open val timestamp: Long = 0L) {
    data class Persisted(
        val remoteModel: ExpenseRemote,
        override val timestamp: Long
    ) : ExpenseSyncRemoteModel(timestamp)

    data class Deleted(
        val id: String,
        override val timestamp: Long
    ) : ExpenseSyncRemoteModel(timestamp)
}
