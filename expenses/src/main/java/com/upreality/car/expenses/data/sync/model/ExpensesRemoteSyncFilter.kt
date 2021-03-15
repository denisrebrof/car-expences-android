package com.upreality.car.expenses.data.sync.model

import java.util.*

sealed class ExpensesRemoteSyncFilter(val date: Date) {
    data class Created(val fromTime: Date) : ExpensesRemoteSyncFilter(fromTime)
    data class Updated(val fromTime: Date) : ExpensesRemoteSyncFilter(fromTime)
    data class Deleted(val fromTime: Date) : ExpensesRemoteSyncFilter(fromTime)
}
