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
    private val converter: ExpenseFilteringInputConverter,
    factory: ExpenseFilteringFormFactory
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val defaultRangeSelection = DateRangeSelection.Week

    private val form = factory.setDefaultRange(defaultRangeSelection).create()

    private val viewStateProcessor = BehaviorProcessor.create<ExpenseFilteringViewState>()
    private val actionsProcessor = PublishProcessor.create<ExpenseFilteringAction>()

    init {
        createViewStateFlow()
    }

    private fun createViewStateFlow() {
        form.getStateMapFlow().map { stateMap ->
            val range = stateMap.getFieldState(ExpenseFilteringKeys.Range).getOrNull()!!
            val type = stateMap.getFieldState(ExpenseFilteringKeys.Type).getOrNull()!!
            return@map ExpenseFilteringViewState(dateRangeState = range, typeState = type)
        }.distinctUntilChanged().doOnNext { viewState ->
            converter
                .toFiltersList(viewState.dateRangeState, viewState.typeState).getOrNull()
                ?.let(ExpenseFilteringAction::ApplyFilters)
                ?.let(actionsProcessor::onNext)
        }.subscribeWithLogError(viewStateProcessor::onNext).let(composite::add)
    }

    fun execute(intent: ExpenseFilteringIntent) {
        when (intent) {
            ShowDateRange -> submitDateRangePicker()
            DropFilters -> dropFilters()
            is SetTypeFilter -> submitTypeFilterEntry(intent.type, intent.available)
            is ApplyDateRange -> form.submit(ExpenseFilteringKeys.Range, intent.selection)
        }
    }

    private fun dropFilters() {
        form.apply {
            val mask = ExpenseType.values().toList().let(::ExpenseFilteringTypeMask)
            submit(ExpenseFilteringKeys.Type, mask)
            submit(ExpenseFilteringKeys.Range, defaultRangeSelection)
        }
    }

    private fun submitTypeFilterEntry(type: ExpenseType, value: Boolean) {
        form.getStateMapFlow().firstElement().map { stateMap ->
            stateMap.getFieldState(ExpenseFilteringKeys.Type)
        }.subscribeWithLogError { inputState ->
            val types = inputState.getOrNull()?.validValueOrNull() ?: setOf()
            val mask = ExpenseFilteringTypeMask(types.toList())
            mask.setType(type, value)
            form.submit(ExpenseFilteringKeys.Type, mask)
        }.let(composite::add)
    }

    private fun submitDateRangePicker() {
        form.getStateMapFlow().firstElement().map { stateMap ->
            stateMap.getFieldState(ExpenseFilteringKeys.Range)
        }.subscribeWithLogError { inputState ->
            val defaultRange = DateRange(
                dateTimeInteractor.getDaysAgo(7),
                dateTimeInteractor.getToday()
            )
            val range = inputState.getOrNull()?.validValueOrNull() ?: defaultRange
            ExpenseFilteringAction.ShowRangePicker(
                range.startDate.time,
                range.endDate.time,
            ).let(actionsProcessor::onNext)
        }.let(composite::add)
    }

    fun getViewState(): Flowable<ExpenseFilteringViewState> = viewStateProcessor
    fun getActionsFlow(): Flowable<ExpenseFilteringAction> = actionsProcessor

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}