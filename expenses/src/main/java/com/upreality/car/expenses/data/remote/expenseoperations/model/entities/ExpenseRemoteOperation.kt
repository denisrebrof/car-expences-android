package com.upreality.car.expenses.data.remote.expenseoperations.model.entities

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ExpenseRemoteOperation(
    val id: String = String(),
    val expenseId: String = String(),
    val type: ExpenseRemoteOperationType = ExpenseRemoteOperationType.Updated,
    @ServerTimestamp
    val timestamp: Date? = null
)