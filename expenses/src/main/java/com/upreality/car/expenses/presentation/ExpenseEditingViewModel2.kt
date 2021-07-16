package com.upreality.car.expenses.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import presentation.SavedStateHandleItemProcessor
import javax.inject.Inject

class ExpenseEditingViewModel2 @Inject constructor(
    handle: SavedStateHandle,
    private val expensesInteractor: IExpensesInteractor,
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val selectedExpenseId = handle.get<Long>(ExpenseEditingActivity.EXPENSE_ID)
    private val expenseMaybe = selectedExpenseId?.let(expensesInteractor::getExpenseMaybe)

    private val initialViewState = ExpenseEditingViewState(false, expenseMaybe != null)
    private val viewStateFlow: BehaviorProcessor<ExpenseEditingViewState>
            by SavedStateHandleItemProcessor(handle, composite, initialViewState)

    fun getViewState(): Flowable<ExpenseEditingViewState> {
        val stateFromExpense = expenseMaybe?.map(ExpenseEditingInputStateConverter::fromExpense)
        return stateFromExpense?.flatMapPublisher {
            viewStateFlow
        }
    }

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }


}

