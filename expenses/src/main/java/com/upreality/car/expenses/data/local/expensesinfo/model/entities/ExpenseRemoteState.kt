package com.upreality.car.expenses.data.local.expensesinfo.model.entities

enum class ExpenseRemoteState(val id: Int) {
    Created(0),
    Persists(1),
    Deleted(2)
}