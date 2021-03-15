package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote

data class ExpenseRemoteSyncModel(
    val remoteModel: ExpenseRemote,
    val operationType: ExpenseOperationFirestoreType,
    val timestamp: Long = 0L
)
