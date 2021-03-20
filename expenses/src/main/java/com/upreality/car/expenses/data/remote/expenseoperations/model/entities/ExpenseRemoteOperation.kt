package com.upreality.car.expenses.data.remote.expenseoperations.model.entities

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ExpenseRemoteOperation(
    val id: String,
    val expenseId: String,
    val type: ExpenseRemoteOperationType,
    @ServerTimestamp
    val timestamp: Date? = null
)