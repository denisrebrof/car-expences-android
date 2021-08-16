package com.upreality.stats.data

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.stats.domain.IStatsRepository
import io.reactivex.Flowable
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val expensesRepository: IExpensesRepository
) : IStatsRepository {

    override fun getRatePerMile(filters: List<ExpenseFilter>): Flowable<Float> {
        return expensesRepository.get(filters).map(this::getRatePerMile)
    }

    override fun getRatePerLiter(filters: List<ExpenseFilter>): Flowable<Float> {
        return expensesRepository.get(filters).map(this::getRatePerLiter)
    }

    override fun getTypesRateMap(filters: List<ExpenseFilter>): Flowable<Map<ExpenseType, Float>> {
        return expensesRepository.get(filters).map { expenses ->
            ExpenseType.values().map { type ->
                val selector: (Expense) -> Boolean = { ExpenseToTypeConverter.toType(it) == type }
                type to expenses.filter(selector).sumOf { it.cost.toDouble() }.toFloat()
            }.toMap().let(this::normalizeMap)
        }
    }

    override fun getRateLastMonth(filters: List<ExpenseFilter>): Flowable<Float> {
        return expensesRepository.get(filters).map(this::getRatePerLiter)
    }

    override fun getRatePerMonth(filters: List<ExpenseFilter>): Flowable<Float> {
        return expensesRepository.get(filters).map(this::getRatePerLiter)
    }

    private fun getRatePerMonth(expenses: List<Expense>): Float{

    }

    private fun getRatePerMile(expenses: List<Expense>): Float {
        val mileageExpenses = expenses.filter { expense ->
            expense is Expense.Fuel || expense is Expense.Maintenance
        }
        val averageCost = mileageExpenses.sumOf { it.cost.toDouble() }.toFloat()

        val earliestMileage = mileageExpenses
            .minByOrNull(Expense::date)
            ?.let(this::getMileageOrNull)
            ?: 0f
        val oldestMileage = mileageExpenses
            .maxByOrNull(Expense::date)
            ?.let(this::getMileageOrNull)
            ?: 0f
        val mileageRange = (oldestMileage - earliestMileage)
        if (mileageRange <= 0f)
            return 0f
        return averageCost / mileageRange
    }

    private fun getMileageOrNull(expense: Expense): Float? {
        return when (expense) {
            is Expense.Fuel -> expense.mileage
            is Expense.Maintenance -> expense.mileage
            else -> null
        }
    }

    private fun <T> normalizeMap(map: Map<T, Float>): Map<T, Float> {
        val average = map.values.sum()
        return map.mapValues { (key, value) -> if (average > 0) value / average else 0f }
    }

    private fun getRatePerLiter(expenses: List<Expense>): Float {
        val fuelExpenses = expenses.filterIsInstance(Expense.Fuel::class.java)
        val averageRate = fuelExpenses.sumByDouble { it.cost.toDouble() }.toFloat()
        val averageLiters = fuelExpenses.sumByDouble { it.liters.toDouble() }.toFloat()
        if (averageLiters <= 0f)
            return 0f
        return averageRate / averageLiters
    }
}