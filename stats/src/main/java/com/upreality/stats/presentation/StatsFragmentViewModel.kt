package com.upreality.stats.presentation

import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.stats.domain.DateRange
import com.upreality.stats.domain.StatValues
import com.upreality.stats.domain.StatsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.DateTimeInteractor
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

@HiltViewModel
class StatsFragmentViewModel @Inject constructor(
    dateTimeInteractor: DateTimeInteractor,
    private val interactor: StatsInteractor
) : ViewModel() {

    private val dateRangeProcessor = DateRange(
        dateTimeInteractor.getToday(),
        dateTimeInteractor.getYesterday()
    ).let { range -> BehaviorProcessor.createDefault(range) }

    fun getViewState(): Flowable<StatsViewState> {
        return dateRangeProcessor.switchMap(this::getStatsStateFlow)
    }

    private fun getStatsStateFlow(range: DateRange): Flowable<StatsViewState> {
        return Flowable.combineLatest(
            interactor.getStatValue(StatValues.RatePerMile, range),
            interactor.getStatValue(StatValues.RatePerMile, range),
            interactor.getTypesMap(range)
        ) { ratePerMile, ratePerLiter, typesMap ->
            StatsViewState(
                dateRange = range,
                ratePerMile = ratePerMile,
                ratePerLiter,
                typesMap.mapKeys { (type, percent) -> type.getTitle() }
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
            is StatsIntents.SetRange -> dateRangeProcessor.onNext(intent.range)
        }
    }

}