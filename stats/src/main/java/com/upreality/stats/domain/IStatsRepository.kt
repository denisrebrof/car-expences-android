package com.upreality.stats.domain

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import com.upreality.car.expenses.domain.model.ExpenseFilter
import io.reactivex.Flowable

interface IStatsRepository
{
    fun getRatePerMile(filters: List<ExpenseFilter>): Flowable<Float>
    fun getRatePerLiter(filters: List<ExpenseFilter>): Flowable<Float>
    fun getTypesRateMap(filters: List<ExpenseFilter>): Flowable<Map<ExpenseType, Float>>
    fun getRateLastMonth(filters: List<ExpenseFilter>): Flowable<Float>
    fun getRatePerMonth(filters: List<ExpenseFilter>): Flowable<Float>
}