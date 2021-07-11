package com.upreality.car.expenses.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import com.upreality.car.expenses.presentation.ExpenseEditingActivity.Companion.EXPENSE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.processors.BehaviorProcessor
import presentation.InputState
import presentation.SavedStateItemProcessorWrapper
import presentation.SavedStateItemProcessorWrapperDelegate
import java.security.InvalidParameterException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseEditingViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
) : ViewModel() {

    val selectedState: SelectedExpenseState by lazy {
        handle.get<Long>(EXPENSE_ID)
            ?.let(SelectedExpenseState::Defined)
            ?: SelectedExpenseState.NotDefined
    }

    private val selectedExpenseTypeId: SavedStateItemProcessorWrapper<Int>
            by SavedStateItemProcessorWrapperDelegate(handle, ExpenseType.Fuel.id)

    private val initialInputState = selectedExpenseTypeId.getValue()
        ?.let(this::checkSelectedTypeInput) ?: InputState.Empty
    private val viewStateFlow = ExpenseEditingInputState(typeInputState = initialInputState)
        .let { ExpenseEditingViewState(false, it) }
        .let { BehaviorProcessor.createDefault(it) }

    fun getViewStateFlow(): Flowable<ExpenseEditingViewState> = viewStateFlow

    fun setTypeInput(type: ExpenseType) {
        type.id.let(this::checkSelectedTypeInput).let { inputState ->
            viewStateFlow.value?.inputState
                ?.copy(typeInputState = inputState)
                ?.let(this::updateInputState)
        }
    }

    fun setCostInput(text: String) {
        checkFloatInput(text).let { inputState ->
            viewStateFlow.value?.inputState
                ?.copy(costInputState = inputState)
                ?.let(this::updateInputState)
        }
    }

    fun setLitersInput(text: String) {
        checkFloatInput(text).let { inputState ->
            viewStateFlow.value?.inputState
                ?.copy(litersInputState = inputState)
                ?.let(this::updateInputState)
        }
    }

    fun setMileageInput(text: String) {
        checkFloatInput(text).let { inputState ->
            viewStateFlow.value?.inputState
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

        val type = inputState.typeInputState as? InputState.Valid ?: return Result.failure(ex)

        val expense = when (type.input) {
            ExpenseType.Fines -> {
                Expense.Fine(
                    date = Date(),
                    cost = cost.input,
                    type = FinesCategories.Other
                )
            }
            ExpenseType.Fuel -> {
                val liters =
                    inputState.litersInputState as? InputState.Valid ?: return Result.failure(ex)
                val mileage =
                    inputState.mileageInputState as? InputState.Valid ?: return Result.failure(ex)
                Expense.Fuel(
                    date = Date(),
                    cost = cost.input,
                    liters = liters.input,
                    mileage = mileage.input
                )
            }
            else -> null
        } ?: return Result.failure(ex)

        return Result.success(expense)
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

    private fun checkSelectedTypeInput(typeId: Int): InputState<ExpenseType> {
        val type = ExpenseType.values()
            .getOrNull(typeId)
            ?: return InputState.Invalid("Invalid input")
        return InputState.Valid(type)
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
)

sealed class SelectedExpenseState {
    object NotDefined : SelectedExpenseState()
    data class Defined(val id: Long) : SelectedExpenseState()
}