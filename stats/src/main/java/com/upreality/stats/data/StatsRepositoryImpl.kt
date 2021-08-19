package com.upreality.stats.data

import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.stats.domain.IStatsRepository
import domain.OptionalValue
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

    override fun getRate(filters: List<ExpenseFilter>): Flowable<Float> {
        return expensesRepository.get(filters).map(this::getRate)
    }

    private fun getRate(expenses: List<Expense>): Float {
        return expenses.sumByDouble { it.cost.toDouble() }.toFloat()
    }

    private fun getRatePerMile(expenses: List<Expense>): Float {
        val mileageExpenses = expenses.filter { expense ->
            expense is Expense.Fuel || expense is Expense.Maintenance
        }
        val averageCost = mileageExpenses.sumOf { it.cost.toDouble() }.toFloat()

        val minMileage = mileageExpenses.minOfOrNull { getMileageOrNull(it) ?: 0f } ?: 0f
        val maxMileage = mileageExpenses.maxOfOrNull { getMileageOrNull(it) ?: 0f } ?: 0f
        val mileageRange = (maxMileage - minMileage)
        if (mileageRange <= 0f)
            return 0f
        return averageCost / mileageRange
    }

    private fun getMileageOrNull(expense: Expense): Float? {
        return when (expense) {
            is Expense.Fuel -> (expense.fuelAmount as? OptionalValue.Defined<Float>)?.value
            is Expense.Maintenance -> (expense.mileage as? OptionalValue.Defined<Float>)?.value
            else -> null
        }
    }

    private fun <T> normalizeMap(map: Map<T, Float>): Map<T, Float> {
        val average = map.values.sum()
        return map.mapValues { (key, value) -> if (average > 0) value / average else 0f }
    }

    private fun OptionalValue<Float>.getValueOrNull() : Float? {
        return when(this){
            is OptionalValue.Defined -> this.value
            OptionalValue.Undefined -> null
        }
    }

    private fun getRatePerLiter(expenses: List<Expense>): Float {
        val fuelExpenses = expenses.filterIsInstance(Expense.Fuel::class.java)
        val fuelMeasurableExpenses = fuelExpenses.filter { it.fuelAmount is OptionalValue.Defined }
        val averageRate = fuelMeasurableExpenses.sumByDouble { it.cost.toDouble() }.toFloat()
        val averageLiters = fuelMeasurableExpenses.sumByDouble {
            (it.fuelAmount as OptionalValue.Defined<Float>).value.toDouble()
        }.toFloat()
        if (averageLiters <= 0f)
            return 0f
        return averageRate / averageLiters
    }
}
