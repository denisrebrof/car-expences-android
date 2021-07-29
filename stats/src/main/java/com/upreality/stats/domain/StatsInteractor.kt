package com.upreality.stats.domain

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import io.reactivex.Flowable
import javax.inject.Inject

class StatsInteractor @Inject constructor(
    private val repository: IStatsRepository
) {

    fun getTypesMap(range: DateRange): Flowable<Map<ExpenseType, Float>> {
        return repository.getTypesRateMap(range)
    }

    fun getStatValue(value: StatValues, range: DateRange): Flowable<Float> {
        return when (value) {
            StatValues.RatePerMile -> repository.getRatePerMile(range)
            StatValues.RatePerLiter -> repository.getRatePerLiter(range)
        }
    }
}