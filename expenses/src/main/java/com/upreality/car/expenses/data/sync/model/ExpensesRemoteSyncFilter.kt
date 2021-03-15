package com.upreality.car.expenses.data.sync.model

import java.util.*

sealed class ExpensesRemoteSyncFilter {
    data class Id(val id: String) : ExpensesRemoteSyncFilter()
    data class FromTime(val fromTime: Date) : ExpensesRemoteSyncFilter()
}
