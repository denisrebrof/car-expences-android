package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote

sealed class ExpenseRemoteSyncOperationModel(val tstamp: Long = 0L) {
    data class Create(val remoteModel: ExpenseRemote, val timestamp: Long) :
        ExpenseRemoteSyncOperationModel(timestamp)

    data class Update(val remoteModel: ExpenseRemote, val timestamp: Long) :
        ExpenseRemoteSyncOperationModel(timestamp)

    data class Delete(val remoteModelId: String, val timestamp: Long) :
        ExpenseRemoteSyncOperationModel(timestamp)
}
