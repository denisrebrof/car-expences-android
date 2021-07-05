package com.upreality.car.expenses.presentation

import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import io.reactivex.Flowable
import javax.inject.Inject

class RefreshExpensesRealmEventProvider @Inject constructor(
    val repository: IExpensesRepository,
) : ExpensesListFragmentViewModel.IRefreshExpensesListEventProvider {
    override fun getRefreshFlow(): Flowable<Unit> {
        return repository.get(ExpenseFilter.All).map {}
    }
}