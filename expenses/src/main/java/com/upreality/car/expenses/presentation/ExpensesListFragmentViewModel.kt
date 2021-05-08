package com.upreality.car.expenses.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.flowable
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.ExpensesInteractorImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpensesListFragmentViewModel @Inject constructor(
    //TODO fix injection
    private val interactor: ExpensesInteractorImpl,
    private val sourceFactory: IExpensesPagingSourceFactory,
    //TODO fix injection
    private val refreshEventProvider: RefreshExpensesEventProviderImpl
) : ViewModel() {

    private var lastSource: PagingSource<Int, Expense>? = null

    @ExperimentalCoroutinesApi
    fun getExpensesFlow(): Flowable<PagingData<Expense>> {
        return Pager(
            PagingConfig(
                pageSize = 6,
                initialLoadSize = 6
            )
        ) {
            sourceFactory.get().also { lastSource = it }
        }.flowable.cachedIn(viewModelScope)
    }

    fun createDebugExpense(): Completable {
        val expense = Expense.Fuel(Date(), 200f, 20f, 2F)
        return interactor.createExpense(expense)
    }

    fun getRefreshFlow(): Flowable<Unit>{
        return refreshEventProvider.getRefreshFlow().map {
            refresh()
        }
    }

    fun refresh() {
        lastSource?.invalidate()
    }

    interface IExpensesPagingSourceFactory {
        fun get(): PagingSource<Int, Expense>
    }

    interface IRefreshExpensesListEventProvider {
        fun getRefreshFlow(): Flowable<Unit>
    }
}