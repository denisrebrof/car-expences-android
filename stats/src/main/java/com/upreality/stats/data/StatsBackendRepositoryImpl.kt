package com.upreality.stats.data

import android.util.Log
import com.upreality.car.expenses.data.shared.converters.ExpenseTypeConverter
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.stats.domain.IStatsRepository
import io.reactivex.Flowable
import javax.inject.Inject

class StatsBackendRepositoryImpl @Inject constructor(
    private val api: StatsBackendApi
) : IStatsRepository {

    override fun getRatePerMile(filters: List<ExpenseFilter>): Flowable<Float> =
        getRequest(filters).let(api::getRatePerMile).doOnNext {
            Log.d("", "")
        }

    override fun getRatePerLiter(filters: List<ExpenseFilter>): Flowable<Float> =
        getRequest(filters).let(api::getRatePerLiter).doOnNext {
            Log.d("", "")
        }

    override fun getTypesRateMap(filters: List<ExpenseFilter>) = getRequest(filters)
        .let(api::getTypesRateMap)
        .map(StatsTypeRateResponse::map)
        .map { map ->
            map.associateBy { it._id }
                .mapKeys{ it.key.let(ExpenseTypeConverter::fromId) }
                .mapValues { it.value.count }
        }

    override fun getRate(filters: List<ExpenseFilter>): Flowable<Float> =
        getRequest(filters).let(api::getRate).doOnNext {
            Log.d("", "")
        }

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