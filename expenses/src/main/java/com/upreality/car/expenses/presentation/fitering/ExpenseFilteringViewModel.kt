package com.upreality.car.expenses.presentation.fitering

import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringIntent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.DateTimeInteractor
import domain.subscribeWithLogError
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseFilteringViewModel @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor,
    factory: ExpenseFilteringFormFactory
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val defaultRange = DateRange(
        dateTimeInteractor.getToday(),
        dateTimeInteractor.getDaysAgo(7)
    )

    private val form = factory.setDefaultRange(defaultRange).create()

    private val viewStateProcessor = BehaviorProcessor.create<ExpenseFilteringViewState>()
    private val actionsProcessor = PublishProcessor.create<ExpenseFilteringAction>()

    init {
        createViewStateFlow()
    }

    fun getViewState(): Flowable<ExpenseFilteringViewState> = viewStateProcessor
    fun getActionState(): Flowable<ExpenseFilteringAction> = actionsProcessor

    fun execute(intent: ExpenseFilteringIntent) {
        when (intent) {
            ShowDateRange -> submitDateRangePicker()
            DropFilters -> dropFilters()
            is SetTypeFilter -> submitTypeFilterEntry(intent.type, intent.available)
            is ApplyDateRange -> submitDateSelection(intent.selection)
        }
    }

    private fun submitDateSelection(selection: DateRangeSelection){
        val dateRange = when(selection){
            DateRangeSelection.AllTime -> TODO()
            is DateRangeSelection.CustomRange -> TODO()
            DateRangeSelection.Month -> TODO()
            DateRangeSelection.Season -> TODO()
            DateRangeSelection.Week -> TODO()
            DateRangeSelection.Year -> dateTimeInteractor.getDaysAgo()
        }

        form.submit(ExpenseFilteringKeys.Range, dateRange)
    }

    private fun dropFilters() {
        form.apply {
            val mask = ExpenseType.values().toList().let(::ExpenseFilteringTypeMask)
            submit(ExpenseFilteringKeys.Type, mask)
            submit(ExpenseFilteringKeys.Range, defaultRange)
        }
    }

    private fun

    private fun submitTypeFilterEntry(type: ExpenseType, value: Boolean) {
        form.getStateMapFlow().map { stateMap ->
            stateMap.getFieldState(ExpenseFilteringKeys.Type)
        }.subscribeWithLogError { inputState ->
            val types = inputState.getOrNull()?.validValueOrNull() ?: setOf()
            val mask = ExpenseFilteringTypeMask(types.toList())
            form.submit(ExpenseFilteringKeys.Type, mask)
        }.let(composite::add)
    }

    private fun submitDateRangePicker() {
        form.getStateMapFlow().map { stateMap ->
            stateMap.getFieldState(ExpenseFilteringKeys.Range)
        }.subscribeWithLogError { inputState ->
            val range = inputState.getOrNull()?.validValueOrNull() ?: defaultRange
            ExpenseFilteringAction.ShowRangePicker(
                range.startDate.time,
                range.endDate.time,
            ).let(actionsProcessor::onNext)
        }.let(composite::add)
    }

    private fun createViewStateFlow() {
        form.getStateMapFlow().map { stateMap ->
            val range = stateMap.getFieldState(ExpenseFilteringKeys.Range).getOrNull()!!
            val type = stateMap.getFieldState(ExpenseFilteringKeys.Type).getOrNull()!!
            return@map ExpenseFilteringViewState(dateRangeState = range, typeState = type)
        }.distinctUntilChanged()
            .subscribeWithLogError(viewStateProcessor::onNext)
            .let(composite::add)
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }

}