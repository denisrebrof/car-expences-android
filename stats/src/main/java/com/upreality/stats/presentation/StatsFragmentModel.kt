package com.upreality.stats.presentation

import com.upreality.stats.domain.DateRange

data class StatsViewState(
    val dateRange: DateRange,
    val ratePerMile: Float = 0f,
    val ratePerLiter: Float = 0f,
    val typesRelationMap: Map<String, Float> = mapOf()
)

sealed class StatsIntents {
    data class SetRange(val range: DateRange) : StatsIntents()
}