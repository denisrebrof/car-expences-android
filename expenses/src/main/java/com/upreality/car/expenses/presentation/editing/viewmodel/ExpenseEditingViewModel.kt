package com.upreality.car.expenses.presentation.editing.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.ExpenseToTypeConverter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.editing.ExpenseEditingNavigator.Companion.EXPENSE_ID
import com.upreality.car.expenses.presentation.editing.ExpenseEditingNavigator.Companion.EXPENSE_TYPE
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue.Today
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue.Yesterday
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.*
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.DateTimeInteractor
import domain.subscribeWithLogError
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import presentation.*
import java.util.*
import javax.inject.Inject
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue as DateInputValue

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class ExpenseEditingViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
    private val dateTimeInteractor: DateTimeInteractor,
    private val converter: ExpenseEditingInputConverter,
    factory: ExpenseEditingInpFormFactory
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val selectedExpenseId = handle.get<Long>(EXPENSE_ID)
    private val expenseMaybe = selectedExpenseId?.let(expensesInteractor::getExpenseMaybe)

    private val defaultExpenseType = handle.get<Int>(EXPENSE_TYPE)
        ?.let { enumValues<ExpenseType>().first { type -> type.id == it } }
        ?: ExpenseType.Fuel
    private val defaultFineType = FinesCategories.Other
    private val defaultDateSelectorState = Today

    @RequiresApi(Build.VERSION_CODES.N)
    private val form = factory.create()

    @RequiresApi(Build.VERSION_CODES.N)
    private val viewStateProcessor = BehaviorProcessor.create<ExpenseEditingViewState>()
    private val actionsProcessor = PublishProcessor.create<ExpenseEditingAction>()

    init {
        createViewStateFlow()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createViewStateFlow() {
        val initFieldsCompletable = expenseMaybe
            ?.doOnSuccess(this::applyExpenseToForm)
            ?.ignoreElement()
            ?: Completable.fromCallable(this::initializeFieldsDefault)

        val viewStateFlow = form.getStateMapFlow().map { stateMap ->
            val parseResult = converter.toExpense(
                stateMap.getFieldState(Cost).getOrNull()!!,
                stateMap.getFieldState(SpendDate).getOrNull()!!,
                stateMap.getFieldState(Type).getOrNull()!!,
                stateMap.getFieldState(Liters).getOrNull()!!,
                stateMap.getFieldState(Mileage).getOrNull()!!,
                stateMap.getFieldState(FineType).getOrNull()!!,
                stateMap.getFieldState(Maintenance).getOrNull()!!,
            )
            return@map ExpenseEditingViewState(
                isValid = parseResult.isSuccess,
                newExpenseCreation = selectedExpenseId == null,
                costState = stateMap.getFieldState(Cost).getOrNull()!!,
                dateState = stateMap.getFieldState(SpendDate).getOrNull()!!,
                typeState = stateMap.getFieldState(Type).getOrNull()!!,
                litersState = stateMap.getFieldState(Liters).getOrNull()!!,
                mileageState = stateMap.getFieldState(Mileage).getOrNull()!!,
                fineTypeState = stateMap.getFieldState(FineType).getOrNull()!!,
                maintenanceTypeState = stateMap.getFieldState(Maintenance).getOrNull()!!,
            )
        }

        initFieldsCompletable
            .andThen(viewStateFlow)
            .distinctUntilChanged()
            .subscribeWithLogError(viewStateProcessor::onNext)
            .let(composite::add)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getViewState(): Flowable<ExpenseEditingViewState> = viewStateProcessor
    fun getActionState(): Flowable<ExpenseEditingAction> = actionsProcessor

    @RequiresApi(Build.VERSION_CODES.N)
    private fun submit() {
        val viewState = viewStateProcessor.value ?: return

        val operation = when (selectedExpenseId) {
            null -> expensesInteractor::createExpense
            else -> expensesInteractor::updateExpense
        }

        val execute = converter.toExpense(
            viewState.costState,
            viewState.dateState,
            viewState.typeState,
            viewState.litersState,
            viewState.mileageState,
            viewState.fineTypeState,
            viewState.maintenanceTypeState
        ).getOrNull()?.apply {
            selectedExpenseId?.let { id = it }
        }?.let(operation) ?: Completable.complete()

        execute.subscribeOn(Schedulers.io()).doOnComplete {
            actionsProcessor.onNext(ExpenseEditingAction.Finish)
        }.subscribeWithLogError().let(composite::add)
    }

    private fun delete() {
        selectedExpenseId ?: return
        selectedExpenseId.let(expensesInteractor::deleteExpense)
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                actionsProcessor.onNext(ExpenseEditingAction.Finish)
            }.subscribeWithLogError().let(composite::add)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeFieldsDefault() {
        form.submit(Type, defaultExpenseType)
        form.submit(FineType, defaultFineType)
        form.submit(SpendDate, defaultDateSelectorState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyExpenseToForm(expense: Expense) {
        form.submit(Cost, expense.cost.toString())
        form.submit(Type, expense.let(ExpenseToTypeConverter::toType))
        val dateInput = when {
            dateTimeInteractor.isToday(expense.date) -> Today
            dateTimeInteractor.isYesterday(expense.date) -> Yesterday
            else -> DateInputValue.Custom(expense.date)
        }
        form.submit(SpendDate, dateInput)
        when (expense) {
            is Expense.Fine -> {
                form.submit(FineType, expense.type)
            }
            is Expense.Fuel -> {
                form.submit(Liters, expense.liters.toString())
                form.submit(Mileage, expense.mileage.toString())
            }
            is Expense.Maintenance -> {
                form.submit(Mileage, expense.mileage.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun execute(intent: ExpenseEditingIntent) {
        when (intent) {
            ExpenseEditingIntent.Close -> actionsProcessor.onNext(
                ExpenseEditingAction.Finish
            )
            ExpenseEditingIntent.Submit -> submit()
            ExpenseEditingIntent.Delete -> delete()
            is ExpenseEditingIntent.SelectDate -> selectDate(intent.type)
        }
    }

    fun <ValueType : Any, OutType : Any> fillForm(
        key: ExpenseEditingKeys<ValueType, OutType>,
        value: ValueType
    ) {
        form.submit(key, value)
    }

    private fun selectDate(type: ExpenseEditingDateSelectionType) {
        when (type) {
            ExpenseEditingDateSelectionType.Today -> form.submit(SpendDate, Today)
            ExpenseEditingDateSelectionType.Yesterday -> form.submit(SpendDate, Yesterday)
            ExpenseEditingDateSelectionType.Custom -> submitDatePicker()
        }
    }

    private fun submitDatePicker() {
        form.getStateMapFlow().map { stateMap ->
            stateMap.getFieldState(SpendDate)
        }.subscribeWithLogError { inputState ->
            val date = inputState.getOrNull()?.validValueOrNull() ?: dateTimeInteractor.getToday()
            ExpenseEditingAction.ShowDatePicker(date.time).let(actionsProcessor::onNext)
        }.let(composite::add)
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}