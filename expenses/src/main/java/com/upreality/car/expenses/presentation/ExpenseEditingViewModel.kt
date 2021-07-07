package com.upreality.car.expenses.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.ExpenseEditingActivity.Companion.EXPENSE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.processors.BehaviorProcessor
import presentation.InputState
import java.security.InvalidParameterException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseEditingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
) : ViewModel() {

    val selectedState: SelectedExpenseState by lazy {
        savedStateHandle.get<Long>(EXPENSE_ID)
            ?.let(SelectedExpenseState::Defined)
            ?: SelectedExpenseState.NotDefined
    }

    private val viewStateFlow = ExpenseEditingViewState(
        false,
        ExpenseEditingInputState()
    ).let { BehaviorProcessor.createDefault(it) }

    private fun getCurrentInputState() = viewStateFlow.value?.inputState

    fun getViewStateFlow(): Flowable<ExpenseEditingViewState> = viewStateFlow

    fun setCostInput(text: String) {
        checkFloatInput(text).let { inputState ->
            getCurrentInputState()
                ?.copy(costInputState = inputState)
                ?.let(this::updateInputState)
        }
    }

    fun setLitersInput(text: String) {
        checkFloatInput(text).let { inputState ->
            getCurrentInputState()
                ?.copy(litersInputState = inputState)
                ?.let(this::updateInputState)
        }
    }

    fun setMileageInput(text: String) {
        checkFloatInput(text).let { inputState ->
            getCurrentInputState()
                ?.copy(mileageInputState = inputState)
                ?.let(this::updateInputState)
        }
    }

    fun getExpense(expenseId: Long): Maybe<Expense> {
        return expensesInteractor
            .getExpensesFlow(ExpenseFilter.Id(expenseId))
            .firstElement()
            .filter(List<Expense>::isNotEmpty)
            .map(List<Expense>::first)
    }

    fun deleteExpense(): Maybe<Result<Unit>> {
        val stubExpense = Expense.Fuel(Date(), 0f, 0f, 0f)
        val expenseId = (selectedState as? SelectedExpenseState.Defined)
            ?.id
            ?: return Maybe.just(Result.failure(java.lang.NullPointerException()))
        return stubExpense
            .also { it.id = expenseId }
            .let(expensesInteractor::deleteExpense)
            .andThen(Maybe.just(Result.success(Unit)))
            .onErrorReturn { Result.failure(it) }
    }

    fun updateExpense(): Maybe<Result<Unit>> {
        val viewState = viewStateFlow.value
            ?: return Result.failure<Unit>(NullPointerException()).let { Maybe.just(it) }
        val expenseId = (selectedState as? SelectedExpenseState.Defined)
            ?.id
            ?: return Maybe.just(Result.failure(java.lang.NullPointerException()))
        return getExpenseFromInput(viewState.inputState).getOrNull()
            ?.also { it.id = expenseId }
            ?.let(expensesInteractor::updateExpense)
            ?.andThen(Maybe.just(Result.success(Unit)))
            ?.onErrorReturn { Result.failure(it) }
            ?: Result.success(Unit).let { Maybe.just(it) }
    }

    fun createExpense(): Maybe<Result<Unit>> {
        val viewState = viewStateFlow.value
            ?: return Result.failure<Unit>(NullPointerException()).let { Maybe.just(it) }

        return getExpenseFromInput(viewState.inputState).getOrNull()
            ?.let(expensesInteractor::createExpense)
            ?.andThen(Maybe.just(Result.success(Unit)))
            ?.onErrorReturn { Result.failure(it) }
            ?: Result.success(Unit).let { Maybe.just(it) }
    }

    private fun getExpenseFromInput(inputState: ExpenseEditingInputState): Result<Expense> {
        val ex = InvalidParameterException("Invalid input")
        val cost = inputState.costInputState as? InputState.Valid ?: return Result.failure(ex)
        val liters = inputState.litersInputState as? InputState.Valid ?: return Result.failure(ex)
        val mileage = inputState.mileageInputState as? InputState.Valid ?: return Result.failure(ex)
        return Expense.Fuel(
            date = Date(),
            cost = cost.input,
            liters = liters.input,
            mileage = mileage.input
        ).let { Result.success(it) }
    }

    private fun updateInputState(inputState: ExpenseEditingInputState) {
        val inputStateValid = getExpenseFromInput(inputState).isSuccess
        ExpenseEditingViewState(inputStateValid, inputState).let(viewStateFlow::onNext)
    }

    private fun checkFloatInput(text: String): InputState<Float> {
        return when {
            text.isEmpty() -> InputState.Empty
            text.toFloatOrNull() == null -> InputState.Invalid("Invalid input")
            else -> InputState.Valid(text.toFloat())
        }
    }
}

data class ExpenseEditingViewState(
    val isValid: Boolean,
    val inputState: ExpenseEditingInputState
)

data class ExpenseEditingInputState(
    val costInputState: InputState<Float> = InputState.Empty,
    val litersInputState: InputState<Float> = InputState.Empty,
    val mileageInputState: InputState<Float> = InputState.Empty,
)

sealed class SelectedExpenseState {
    object NotDefined : SelectedExpenseState()
    data class Defined(val id: Long) : SelectedExpenseState()
}