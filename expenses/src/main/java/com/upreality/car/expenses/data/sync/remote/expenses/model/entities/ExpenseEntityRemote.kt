package com.upreality.car.expenses.data.sync.remote.expenses.model.entities

import com.google.firebase.firestore.DocumentId

data class ExpenseEntityRemote(
    @DocumentId
    val id: String = String(),
    val date: Long = 0L,
    val cost: Float = 0f,
    val type: Int = 0,
    val detailsId: String = String()
)