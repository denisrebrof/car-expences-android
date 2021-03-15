package com.upreality.car.expenses.data.sync.model

sealed class ExpensesLocalSyncFilter{
    data class LocalId(val id: Long) : ExpensesLocalSyncFilter()
    data class RemoteId(val id: String) : ExpensesLocalSyncFilter()
    object StateUpdated : ExpensesLocalSyncFilter()
}