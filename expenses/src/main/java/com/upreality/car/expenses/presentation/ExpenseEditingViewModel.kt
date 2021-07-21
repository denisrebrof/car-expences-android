package com.upreality.car.expenses.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.ExpenseEditingNavigator.Companion.EXPENSE_ID
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.*
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.subscribeWithLogError
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import presentation.*
import presentation.InputForm.RequestFieldInputStateResult.Success
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class ExpenseEditingViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val selectedExpenseId = handle.get<Long>(EXPENSE_ID)
    private val expenseMaybe = selectedExpenseId?.let(expensesInteractor::getExpenseMaybe)

    private val defaultExpense = Expense.Fuel(Date(), 0f, 0f, 0f)

    @RequiresApi(Build.VERSION_CODES.N)
    private val form = InputForm(
        Cost.createFieldPair(String::class),
        Type.createFieldPair(ExpenseType::class),
        Liters.createFieldPair(String::class),
        Mileage.createFieldPair(String::class),
        FineType.createFieldPair(FinesCategories::class),
    )

    private val actionsProcessor = PublishProcessor.create<ExpenseEditingAction>()

    fun getActionState(): Flowable<ExpenseEditingAction> = actionsProcessor

    @RequiresApi(Build.VERSION_CODES.N)
    fun getViewState(): Flowable<ExpenseEditingViewState> {
        val expenseMaybe = expenseMaybe ?: Maybe.just(defaultExpense)
        return expenseMaybe.doOnSuccess(this::applyExpenseToForm).flatMapPublisher {
            val inputStateFlows = arrayOf(
                form.getStateFlow(Cost, String::class) as Success,
                form.getStateFlow(Type, ExpenseType::class) as Success,
                form.getStateFlow(Liters, String::class) as Success,
                form.getStateFlow(Mileage, String::class) as Success,
                form.getStateFlow(FineType, FinesCategories::class) as Success,
            ).map { it.result }
            return@flatMapPublisher Flowable.combineLatest(inputStateFlows) { inputStates ->
                val costState = inputStates[0] as InputState<String>
                val typeState = inputStates[1] as InputState<ExpenseType>
                val litersState = inputStates[2] as InputState<String>
                val mileageState = inputStates[3] as InputState<String>
                val fineTypeState = inputStates[4] as InputState<FinesCategories>

                val parseResult = ExpenseEditingInputConverter.toExpense(
                    costState,
                    typeState,
                    litersState,
                    mileageState,
                    fineTypeState,
                )

                return@combineLatest ExpenseEditingViewState(
                    isValid = parseResult.isSuccess,
                    newExpenseCreation = selectedExpenseId != null,
                    costState = costState,
                    typeState = typeState,
                    litersState = litersState,
                    mileageState = mileageState,
                    fineTypeState = fineTypeState
                )
            }.doOnError {
                Log.d("", "")
            }
        }.distinctUntilChanged()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun submit() {
        getViewState().firstElement().subscribeOn(Schedulers.io()).flatMapCompletable { viewState ->
            val operation = when (selectedExpenseId) {
                null -> expensesInteractor::updateExpense
                else -> expensesInteractor::createExpense
            }
            return@flatMapCompletable ExpenseEditingInputConverter.toExpense(
                viewState.costState,
                viewState.typeState,
                viewState.litersState,
                viewState.mileageState,
                viewState.fineTypeState,
            ).getOrNull()?.apply {
                selectedExpenseId?.let { id = it }
            }?.let(operation) ?: Completable.complete()
        }.doOnComplete {
            actionsProcessor.onNext(ExpenseEditingAction.Finish)
        }.subscribeWithLogError().let(composite::add)
    }

    private fun delete() {
        selectedExpenseId ?: return
        selectedExpenseId.let(expensesInteractor::deleteExpense)
            .subscribeOn(Schedulers.io())
            .subscribeWithLogError()
            .let(composite::add)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyExpenseToForm(expense: Expense) {
        form.submit(expense.cost.toString(), Cost)
        form.submit(expense.let(ExpenseEditingInputConverter::getExpenseType), Type)
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }

    sealed class ExpenseEditingKeys<out ValueType : Any>(id: Int, type: KClass<ValueType>) :
        InputForm.FieldKeys<ValueType>(id) {
        object Cost : ExpenseEditingKeys<String>(0, String::class)
        object Type : ExpenseEditingKeys<ExpenseType>(1, ExpenseType::class)
        object Liters : ExpenseEditingKeys<String>(2, String::class)
        object Mileage : ExpenseEditingKeys<String>(3, String::class)
        object FineType : ExpenseEditingKeys<FinesCategories>(4, FinesCategories::class)
    }

    sealed class ExpenseEditingAction {
        object Finish : ExpenseEditingAction()
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
    }

    data class ExpenseEditingViewState(
        val isValid: Boolean,
        val newExpenseCreation: Boolean,
        val costState: InputState<String>,
        val typeState: InputState<ExpenseType>,
        val litersState: InputState<String>,
        val mileageState: InputState<String>,
        val fineTypeState: InputState<FinesCategories>,
    )

    private fun submitExpenseAction(expense: Expense) {
        val cost = expense.cost.toString()
        val liters = (expense as? Expense.Fuel)?.liters?.toString() ?: ""
        val fineType = (expense as? Expense.Fine)?.type ?: FinesCategories.Other
        val mileage = (expense as? Expense.Fuel)?.mileage?.toString()
            ?: (expense as? Expense.Maintenance)?.mileage?.toString()
            ?: String()
        val type = ExpenseEditingInputConverter.getExpenseType(expense)
//        val action = ExpenseEditingAction.SetupExpense(cost, type, liters, mileage, fineType)
//        actionsProcessor.onNext(action)
    }
}