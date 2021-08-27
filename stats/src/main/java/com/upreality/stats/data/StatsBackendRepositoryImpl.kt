package com.upreality.stats.data

import com.upreality.car.expenses.data.shared.converters.ExpenseTypeConverter
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.stats.domain.IStatsRepository
import javax.inject.Inject

class StatsBackendRepositoryImpl @Inject constructor(
    private val api: StatsBackendApi
) : IStatsRepository {
    override fun getRatePerMile(filters: List<ExpenseFilter>) =
        getRequest(filters).let(api::getRatePerMile)

    override fun getRatePerLiter(filters: List<ExpenseFilter>) =
        getRequest(filters).let(api::getRatePerLiter)

    override fun getTypesRateMap(filters: List<ExpenseFilter>) = getRequest(filters)
        .let(api::getTypesRateMap)
        .map(StatsTypeRateResponse::map)
        .map { map ->
            map.mapKeys { (key, _) ->
                ExpenseTypeConverter.fromId(key.toInt())
            }
        }

    override fun getRate(filters: List<ExpenseFilter>) = getRequest(filters).let(api::getRate)

    private fun getRequest(filters: List<ExpenseFilter>) = StatsBackendRequest(
        getTypeIds(filters),
        filters.filterIsInstance<ExpenseFilter.DateRange>().firstOrNull()?.from?.time,
        filters.filterIsInstance<ExpenseFilter.DateRange>().firstOrNull()?.to?.time
    )

    private fun getTypeIds(filters: List<ExpenseFilter>): List<Long>? {
        val filter = filters.filterIsInstance<ExpenseFilter.Type>().firstOrNull() ?: return null
        return filter.types
            .map(ExpenseToTypeConverter::toType)
            .map(ExpenseTypeConverter::toId)
            .map(Int::toLong)
    }
}