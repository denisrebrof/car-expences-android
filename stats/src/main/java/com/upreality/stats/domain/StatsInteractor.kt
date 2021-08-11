package com.upreality.stats.domain

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import com.upreality.car.expenses.domain.model.ExpenseFilter
import io.reactivex.Flowable
import javax.inject.Inject

class StatsInteractor @Inject constructor(
    private val repository: IStatsRepository
) {

    fun getTypesMap(filters: List<ExpenseFilter>): Flowable<Map<ExpenseType, Float>> {
        return repository.getTypesRateMap(filters)
    }

    fun getStatValue(value: StatValues, filters: List<ExpenseFilter>): Flowable<Float> {
        return when (value) {
            StatValues.RatePerMile -> repository.getRatePerMile(filters)
            StatValues.RatePerLiter -> repository.getRatePerLiter(filters)
        }
    }
}