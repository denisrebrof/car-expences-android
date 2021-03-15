package com.upreality.car.expenses.data.sync.model

import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import java.util.*

data class ExpenseRemoteSyncModel(
    val remoteModel: ExpenseRemote,
    val timestamp: Date
)
