package com.upreality.car.expenses.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.flowable
import com.upreality.car.expenses.data.paging.ExpensesPagingSource
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.ExpensesInteractorImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class ExpensesListFragmentViewModel @Inject constructor(
    //TODO fix injection
    private val interactor: ExpensesInteractorImpl,
    private val sourceFactory: IExpensesPagingSourceFactory,
    //TODO fix injection
    private val refreshEventProvider: RefreshExpensesRealmEventProvider
) : ViewModel() {

    private var lastSource: ExpensesPagingSource? = null

    @ExperimentalCoroutinesApi
    fun getExpensesFlow(): Flowable<PagingData<Expense>> {
        val config = PagingConfig(pageSize = 6, initialLoadSize = 6)
        return Pager(config) {
            sourceFactory.get().also { lastSource = it }
        }.flowable.cachedIn(viewModelScope)
    }

    fun getRefreshFlow(): Flowable<Unit> {
        return refreshEventProvider.getRefreshFlow().doOnNext {
            refresh()
        }
    }

    fun setFilters(filters: List<ExpenseFilter>) {
        lastSource?.filters = filters
        lastSource?.invalidate()
    }

    fun refresh() = lastSource?.invalidate()

    fun deleteExpense(expense: Expense) = interactor.deleteExpense(expense)

    interface IExpensesPagingSourceFactory {
        fun get(): ExpensesPagingSource
    }

    interface IRefreshExpensesListEventProvider {
        fun getRefreshFlow(): Flowable<Unit>
    }
}