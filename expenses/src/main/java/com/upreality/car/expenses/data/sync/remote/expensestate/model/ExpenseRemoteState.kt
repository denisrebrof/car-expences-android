package com.upreality.car.expenses.data.sync.remote.expensestate.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ExpenseRemoteState(
    @DocumentId
    val id: String = String(),
    val remoteId: String = String(),
    val deleted: Boolean = false,
    @ServerTimestamp
    val timestamp: Date? = null
)
