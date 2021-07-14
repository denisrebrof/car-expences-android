package com.upreality.car.expenses.presentation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.ExpenseEditingActivity.Companion.EXPENSE_ID
import com.upreality.car.expenses.presentation.ExpenseEditingIntent.SetInput.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import kotlinx.android.parcel.Parcelize
import presentation.InputState
import presentation.SavedStateItemDelegate
import java.security.InvalidParameterException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseEditingViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val selectedExpenseId = handle.get<Long>(EXPENSE_ID)

    private val selectedExpenseTypeId: Int? by SavedStateItemDelegate(handle)

    private val initialExpenseTypeMaybe = selectedExpenseTypeId?.let { id ->
        ExpenseType.values().firstOrNull { it.id == id }?.let { Maybe.just(it) }
    } ?: selectedExpenseId?.let(this::getExpense)?.map { expense ->
        when (expense) {
            is Expense.Fine -> ExpenseType.Fines
            is Expense.Fuel -> ExpenseType.Fuel
            is Expense.Maintenance -> ExpenseType.Maintenance
        }
    }

    private fun getExpense(expenseId: Long): Maybe<Expense> {
        return expensesInteractor
            .getExpensesFlow(ExpenseFilter.Id(expenseId))
            .firstElement()
            .filter(List<Expense>::isNotEmpty)
            .map(List<Expense>::first)
    }

    private val initialExpenseTypeInputStateMaybe = initialExpenseTypeMaybe?.map { type ->
        InputState.Valid(type)
    } ?: InputState.Empty.let { Maybe.just(it) }

    private val viewStateFlow = BehaviorProcessor.createDefault(
        ExpenseEditingViewState(
            false,
            ExpenseEditingInputState()
        )
    )

    fun getViewStateFlow(): Flowable<ExpenseEditingViewState> {
        val inputState = selectedExpenseTypeId
        val defaultViewStateFlow = ExpenseEditingViewState(
            false,
            ExpenseEditingInputState()
        ).let { Flowable.just(it) }
        val expenseId = selectedExpenseId ?: return defaultViewStateFlow
        val expenseMaybe = getExpense(expenseId)
        val expenseType = selectedExpenseTypeId

        return initialExpenseTypeInputStateMaybe.map {
            ExpenseEditingInputState(typeInputState = it)
        }.map {
            ExpenseEditingViewState(false, it)
        }.flatMapPublisher(viewStateFlow::startWith)
    }

    fun executeIntent(intent: ExpenseEditingIntent) {
        val inputState = when (intent) {
            is ExpenseEditingIntent.SetInput -> executeSetInputIntent(intent)
            else -> return //do nothing
        }
    }

    private fun executeSetInputIntent(intent: ExpenseEditingIntent.SetInput) {
        val currentState = viewStateFlow.value?.inputState ?: return
        val resultState = when (intent) {
            is SetCostInput -> checkFloatInput(intent.input).let { inputState ->
                currentState.copy(costInputState = inputState)
            }
            is SetFineTypeInput -> {
                val categoryState = InputState.Valid(intent.input)
                currentState.copy(fineTypeInputState = categoryState)
            }
            is SetLitersInput -> checkFloatInput(intent.input).let { inputState ->
                currentState.copy(litersInputState = inputState)
            }
            is SetMileageInput -> checkFloatInput(intent.input).let { inputState ->
                currentState.copy(mileageInputState = inputState)
            }
            is SetTypeInput -> intent.input.id.let(this::checkSelectedTypeInput).let { inputState ->
                currentState.copy(typeInputState = inputState)
            }
        }
        updateInputState(resultState)
    }

    fun deleteExpense(): Completable {
        val error = InvalidParameterException().let(Completable::error)
        val expenseId = selectedExpenseId ?: return error
        return expensesInteractor.deleteExpense(expenseId)
    }

    fun updateExpense(): Completable {
        val error = InvalidParameterException().let(Completable::error)
        val expenseId = selectedExpenseId ?: return error
        val expense = getExpenseFromInput().getOrNull()?.also { it.id = expenseId } ?: return error
        return expensesInteractor.updateExpense(expense)
    }

    fun createExpense(): Completable {
        val error = InvalidParameterException().let(Completable::error)
        val expense = getExpenseFromInput().getOrNull() ?: return error
        return expensesInteractor.createExpense(expense)
    }

    private fun updateInputState(inputState: ExpenseEditingInputState) {
        val inputStateValid = getExpenseFromInput().isSuccess
        ExpenseEditingViewState(inputStateValid, inputState).let(viewStateFlow::onNext)
    }

    private fun getExpenseFromInput(): Result<Expense> {
        val exception = InvalidParameterException("Invalid input")
        val failure = Result.failure<Expense>(exception)
        val inputState = viewStateFlow.value?.inputState ?: return failure

        val cost = inputState.costInputState.validOrNull() ?: return failure
        val type = inputState.typeInputState.validOrNull() ?: return failure

        val expense = when (type.input) {
            ExpenseType.Fines -> {
                val fineCategory = inputState.fineTypeInputState.validOrNull() ?: return failure
                Expense.Fine(
                    date = Date(),
                    cost = cost.input,
                    type = fineCategory.input
                )
            }
            ExpenseType.Fuel -> {
                val liters = inputState.litersInputState.validOrNull() ?: return failure
                val mileage = inputState.mileageInputState.validOrNull() ?: return failure
                Expense.Fuel(
                    date = Date(),
                    cost = cost.input,
                    liters = liters.input,
                    mileage = mileage.input
                )
            }
            else -> null
        } ?: return failure

        return Result.success(expense)
    }

    private fun checkFloatInput(text: String): InputState<Float> {
        return when {
            text.isEmpty() -> InputState.Empty
            text.toFloatOrNull() == null -> InputState.Invalid("Invalid input")
            else -> InputState.Valid(text.toFloat())
        }
    }

    private fun checkSelectedTypeInput(typeId: Int): InputState<ExpenseType> {
        val type = ExpenseType.values()
            .getOrNull(typeId)
            ?: return InputState.Invalid("Invalid input")
        return InputState.Valid(type)
    }

    override fun onCleared() {
        if (!composite.isDisposed)
            composite.dispose()
        super.onCleared()
    }
}

data class ExpenseEditingViewState(
    val isValid: Boolean,
    val inputState: ExpenseEditingInputState
)

data class ExpenseEditingInputState(
    val costInputState: InputState<Float> = InputState.Empty,
    val typeInputState: InputState<ExpenseType> = InputState.Empty,
    val litersInputState: InputState<Float> = InputState.Empty,
    val mileageInputState: InputState<Float> = InputState.Empty,
    val fineTypeInputState: InputState<FinesCategories> = InputState.Empty,
)

sealed class ExpenseEditingIntent {
    sealed class SetInput : ExpenseEditingIntent() {
        data class SetTypeInput(val input: ExpenseType) : SetInput()
        data class SetCostInput(val input: String) : SetInput()
        data class SetLitersInput(val input: String) : SetInput()
        data class SetMileageInput(val input: String) : SetInput()
        data class SetFineTypeInput(val input: FinesCategories) : SetInput()
    }
}

@Parcelize
data class ExpenseEditingType(var expenseTypeId: Int?) : Parcelable {
    var expenseType: ExpenseType?
        get() = ExpenseType.values().firstOrNull { it.id == expenseTypeId }
        set(value) {
            expenseTypeId = value?.id
        }
}