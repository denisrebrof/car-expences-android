package com.upreality.stats.presentation

import com.upreality.car.expenses.domain.model.ExpenseFilter

data class StatsViewState(
    val ratePerMile: Float = 0f,
    val ratePerLiter: Float = 0f,
    val typesRelationMap: Map<String, Float> = mapOf()
)

sealed class StatsIntents {
    data class SetFilters(val filters: List<ExpenseFilter>) : StatsIntents()
}