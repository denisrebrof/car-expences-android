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
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class ExpensesListFragmentViewModel @Inject constructor(
    private val sourceFactory: IExpensesPagingSourceFactory
) : ViewModel() {

    @ExperimentalCoroutinesApi
    fun getExpensesFlow(): Flowable<PagingData<Expense>> {
        return Pager(PagingConfig(pageSize = 6)) {
            sourceFactory.get()
        }.flowable.cachedIn(viewModelScope)
    }

    interface IExpensesPagingSourceFactory {
        fun get(): PagingSource<Int, Expense>
    }
}