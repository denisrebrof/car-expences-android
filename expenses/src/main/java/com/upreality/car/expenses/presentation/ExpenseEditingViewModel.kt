package com.upreality.car.expenses.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.ExpenseEditingNavigator.Companion.EXPENSE_ID
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingIntent.DatePickerSelectionType
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.*
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
import presentation.InputForm.RequestFieldInputStateResult.Success
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingDateSelectorState as DateSelectorState

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class ExpenseEditingViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
    private val dateTimeInteractor: DateTimeInteractor,
    private val converter: ExpenseEditingInputConverter
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val selectedExpenseId = handle.get<Long>(EXPENSE_ID)
    private val expenseMaybe = selectedExpenseId?.let(expensesInteractor::getExpenseMaybe)

    private val defaultExpenseType = ExpenseType.Fuel
    private val defaultFineType = FinesCategories.Other
    private val defaultDateSelectorState = DateSelectorState.Today

    @RequiresApi(Build.VERSION_CODES.N)
    private val form = InputForm(
        Cost.createFieldPair(String::class),
        Type.createFieldPair(ExpenseType::class),
        Liters.createFieldPair(String::class),
        Mileage.createFieldPair(String::class),
        FineType.createFieldPair(FinesCategories::class),
    )

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

        val inputStateFlows = arrayOf(
            form.getStateFlow(Cost, String::class) as Success,
            form.getStateFlow(SpendDate, DateSelectorState::class) as Success,
            form.getStateFlow(Type, ExpenseType::class) as Success,
            form.getStateFlow(Liters, String::class) as Success,
            form.getStateFlow(Mileage, String::class) as Success,
            form.getStateFlow(FineType, FinesCategories::class) as Success,
        ).map { it.result }

        val viewStateFlow = Flowable.combineLatest(inputStateFlows) { inputStates ->
            val iterator = inputStates.iterator()
            val costState = iterator.next() as InputState<String>
            val dateState = iterator.next() as InputState<DateSelectorState>
            val typeState = iterator.next() as InputState<ExpenseType>
            val litersState = iterator.next() as InputState<String>
            val mileageState = iterator.next() as InputState<String>
            val fineTypeState = iterator.next() as InputState<FinesCategories>

            val parseResult = converter.toExpense(
                costState,
                dateState,
                typeState,
                litersState,
                mileageState,
                fineTypeState,
            )

            return@combineLatest ExpenseEditingViewState(
                isValid = parseResult.isSuccess,
                newExpenseCreation = selectedExpenseId == null,
                costState = costState,
                dateState = dateState,
                typeState = typeState,
                litersState = litersState,
                mileageState = mileageState,
                fineTypeState = fineTypeState
            )
        }

        initFieldsCompletable
            .andThen(viewStateFlow)
            .distinctUntilChanged()
            .subscribeWithLogError(viewStateProcessor::onNext)
            .let(composite::add)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getViewState() = viewStateProcessor

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
        form.submit(defaultExpenseType, Type)
        form.submit(defaultFineType, FineType)
        form.submit(defaultDateSelectorState, SpendDate)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyExpenseToForm(expense: Expense) {
        form.submit(expense.cost.toString(), Cost)
        form.submit(expense.let(converter::getExpenseType), Type)
        form.submit(expense.date, SpendDate)
        when (expense) {
            is Expense.Fine -> {
                form.submit(expense.type, FineType)
            }
            is Expense.Fuel -> {
                form.submit(expense.liters.toString(), Liters)
                form.submit(expense.mileage.toString(), Mileage)
            }
            is Expense.Maintenance -> {
                form.submit(expense.mileage.toString(), Mileage)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun execute(intent: ExpenseEditingIntent) {
        when (intent) {
            is ExpenseEditingIntent.FillForm<*> -> form.submit(intent.value, intent.key)
            ExpenseEditingIntent.Close -> actionsProcessor.onNext(ExpenseEditingAction.Finish)
            ExpenseEditingIntent.Submit -> submit()
            ExpenseEditingIntent.Delete -> delete()
            is ExpenseEditingIntent.SelectDate -> submitDatePicker(intent.type)
        }
    }

    private fun submitDatePicker(type: DatePickerSelectionType) {
        val dateSelectorStateRequest = form.getStateFlow(SpendDate, DateSelectorState::class)
        val dateSelectorInputState = dateSelectorStateRequest as? Success ?: return
        dateSelectorInputState.result.firstElement().subscribeWithLogError { inputState ->
            val dateSelectorState = (inputState as? InputState.Valid)
                ?.inp
                ?: DateSelectorState.Today
            val startCalendar = when (dateSelectorState) {
                is DateSelectorState.Custom -> dateSelectorState.date
                DateSelectorState.Today -> dateTimeInteractor.getToday()
                DateSelectorState.Yesterday -> dateTimeInteractor.getYesterday()
            }.let { startDate -> Calendar.getInstance().apply { time = startDate } }

            ExpenseEditingAction.ShowDatePicker(
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)
            ).let(actionsProcessor::onNext)
        }
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }

    sealed class ExpenseEditingKeys<out ValueType : Any>(
        id: Int,
        type: KClass<ValueType>
    ) : InputForm.FieldKeys<ValueType>(id) {
        object Cost : ExpenseEditingKeys<String>(0, String::class)
        object SpendDate : ExpenseEditingKeys<DateSelectorState>(1, DateSelectorState::class)
        object Type : ExpenseEditingKeys<ExpenseType>(2, ExpenseType::class)
        object Liters : ExpenseEditingKeys<String>(3, String::class)
        object Mileage : ExpenseEditingKeys<String>(4, String::class)
        object FineType : ExpenseEditingKeys<FinesCategories>(5, FinesCategories::class)
    }

    sealed class ExpenseEditingAction {
        object Finish : ExpenseEditingAction()
        data class ShowDatePicker(
            val year: Int,
            val month: Int,
            val day: Int
        ) : ExpenseEditingAction()
//        data class SetupExpense(
//            val costState: String,
//            val typeState: ExpenseType,
//            val litersState: String,
//            val mileageState: String,
//            val fineTypeState: FinesCategories,
//        ) : ExpenseEditingAction()
    }

    sealed class ExpenseEditingIntent {
        data class FillForm<ValueType : Any>(
            val key: ExpenseEditingKeys<ValueType>,
            val value: ValueType,
            val type: KClass<ValueType>
        ) : ExpenseEditingIntent()

        object Close : ExpenseEditingIntent()
        object Submit : ExpenseEditingIntent()
        object Delete : ExpenseEditingIntent()
        data class SelectDate(val type: DatePickerSelectionType) : ExpenseEditingIntent()

        enum class DatePickerSelectionType{
            Today,
            Yesterday,
            Custom
        }
    }

    data class ExpenseEditingViewState(
        val isValid: Boolean,
        val newExpenseCreation: Boolean,
        val costState: InputState<String>,
        val dateState: InputState<DateSelectorState>,
        val typeState: InputState<ExpenseType>,
        val litersState: InputState<String>,
        val mileageState: InputState<String>,
        val fineTypeState: InputState<FinesCategories>,
    )

    sealed class ExpenseEditingDateSelectorState {
        object Today : DateSelectorState()
        object Yesterday : DateSelectorState()
        data class Custom(val date: Date) : DateSelectorState()
    }

    private fun submitExpenseAction(expense: Expense) {
        val cost = expense.cost.toString()
        val liters = (expense as? Expense.Fuel)?.liters?.toString() ?: ""
        val fineType = (expense as? Expense.Fine)?.type ?: FinesCategories.Other
        val mileage = (expense as? Expense.Fuel)?.mileage?.toString()
            ?: (expense as? Expense.Maintenance)?.mileage?.toString()
            ?: String()
        val type = converter.getExpenseType(expense)
//        val action = ExpenseEditingAction.SetupExpense(cost, type, liters, mileage, fineType)
//        actionsProcessor.onNext(action)
    }
}