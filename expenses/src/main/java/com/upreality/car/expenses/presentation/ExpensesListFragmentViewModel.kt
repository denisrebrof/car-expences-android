package com.upreality.car.expenses.presentation

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpensesListFragmentViewModel @Inject constructor(
    //TODO fix injection
    private val interactor: ExpensesInteractorImpl,
    private val sourceFactory: IExpensesPagingSourceFactory
) : ViewModel() {

    @ExperimentalCoroutinesApi
    fun getExpensesFlow(): Flowable<PagingData<Expense>> {
        return Pager(PagingConfig(pageSize = 6)) {
            sourceFactory.get()
        }.flowable.cachedIn(viewModelScope)
    }

    fun createDebugExpense(): Completable {
        val expense = Expense.Fuel(Date(), 200f, 20f, 2F)
        return interactor.createExpense(expense)
    }

    interface IExpensesPagingSourceFactory {
        fun get(): PagingSource<Int, Expense>
    }
}