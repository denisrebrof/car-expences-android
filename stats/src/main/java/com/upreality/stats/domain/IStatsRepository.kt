package com.upreality.stats.domain

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import io.reactivex.Flowable

interface IStatsRepository
{
    fun getRatePerMile(range: DateRange): Flowable<Float>
    fun getRatePerLiter(range: DateRange): Flowable<Float>
    fun getTypesRateMap(range: DateRange): Flowable<Map<ExpenseType, Float>>
}