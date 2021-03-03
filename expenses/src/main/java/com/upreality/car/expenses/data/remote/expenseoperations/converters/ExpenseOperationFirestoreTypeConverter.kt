package com.upreality.car.expenses.data.remote.expenseoperations.converters

import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestoreType

class ExpenseOperationFirestoreTypeConverter {
    fun toId(type: ExpenseOperationFirestoreType) = when (type) {
        ExpenseOperationFirestoreType.Created -> 0
        ExpenseOperationFirestoreType.Updated -> 1
        ExpenseOperationFirestoreType.Deleted -> 2
    }

    fun fromId(id: Int) = when (id) {
        0 -> ExpenseOperationFirestoreType.Created
        2 -> ExpenseOperationFirestoreType.Deleted
        else -> ExpenseOperationFirestoreType.Updated
    }
}