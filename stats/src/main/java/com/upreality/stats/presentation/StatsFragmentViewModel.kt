package com.upreality.stats.presentation

import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.stats.domain.StatValues
import com.upreality.stats.domain.StatsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

@HiltViewModel
class StatsFragmentViewModel @Inject constructor(
    private val interactor: StatsInteractor
) : ViewModel() {

    private val filtersProcessor = BehaviorProcessor.createDefault<List<ExpenseFilter>>(listOf())

    fun getViewState(): Flowable<StatsViewState> {
        return filtersProcessor.switchMap(this::getStatsStateFlow)
    }

    private fun getStatsStateFlow(filters: List<ExpenseFilter>): Flowable<StatsViewState> {
        return Flowable.combineLatest(
            interactor.getStatValue(StatValues.RatePerMile, filters),
            interactor.getStatValue(StatValues.RatePerLiter, filters),
            interactor.getStatValue(StatValues.Rate, filters),
            interactor.getTypesMap(filters)
        ) { ratePerMile, ratePerLiter, rate, typesMap ->
            StatsViewState(
                ratePerMile = ratePerMile,
                ratePerLiter = ratePerLiter,
                rate = rate,
                typesRelationMap = typesMap.mapKeys { (type, percent) -> type.getTitle() }
            )
        }
    }

    private fun ExpenseType.getTitle(): String {
        return when (this) {
            ExpenseType.Fines -> "Fines"
            ExpenseType.Fuel -> "Fuel"
            ExpenseType.Maintenance -> "Maintenance"
        }
    }

    fun execute(intent: StatsIntents) {
        when (intent) {
            is StatsIntents.SetFilters -> filtersProcessor.onNext(intent.filters)
        }
    }

}