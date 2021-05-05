package com.upreality.car.expenses.data.paging

import androidx.paging.PagingSource
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.presentation.ExpensesListFragmentViewModel
import javax.inject.Inject

class ExpensesPagingSourceFactoryImpl @Inject constructor(
    private val repository: IExpensesRepository
) : ExpensesListFragmentViewModel.IExpensesPagingSourceFactory {
    override fun get(): PagingSource<Int, Expense> {
        return ExpensesPagingSource(repository = repository)
    }
}