package com.upreality.car.expenses.data.sync.model

import java.util.*

sealed class ExpenseRemoteSyncFilter {
    data class Id(val id: String) : ExpenseRemoteSyncFilter()
    data class FromTime(val fromTime: Date) : ExpenseRemoteSyncFilter()
}