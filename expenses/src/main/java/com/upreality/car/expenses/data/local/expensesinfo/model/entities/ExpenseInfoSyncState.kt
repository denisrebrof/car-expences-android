package com.upreality.car.expenses.data.local.expensesinfo.model.entities

enum class ExpenseInfoSyncState(val id: Int) {
    Created(0),
    Persists(1),
    Updated(2),
    Deleted(3)
}