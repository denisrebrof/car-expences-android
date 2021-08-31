package com.upreality.stats.data

import com.upreality.car.expenses.data.shared.model.ExpenseType

data class StatsTypeRateResponse(
    val map: List<StatsTypeRateResponseMapEntry>? = null
)

data class StatsTypeRateResponseMapEntry(
    val _id: Int,
    val count: Float
)