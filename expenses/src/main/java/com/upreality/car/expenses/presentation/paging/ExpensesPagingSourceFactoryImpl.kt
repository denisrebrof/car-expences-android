package com.upreality.car.expenses.presentation.paging

import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.presentation.list.ExpensesListFragmentViewModel
import javax.inject.Inject

class ExpensesPagingSourceFactoryImpl @Inject constructor(
    private val repository: IExpensesRepository
) : ExpensesListFragmentViewModel.IExpensesPagingSourceFactory {

    override fun get(): ExpensesPagingSource {
        return ExpensesPagingSource(repository = repository)
    }


}