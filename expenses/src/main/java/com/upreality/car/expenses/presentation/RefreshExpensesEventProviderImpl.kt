package com.upreality.car.expenses.presentation

import com.upreality.car.expenses.data.sync.IExpensesSyncTimestampProvider
import io.reactivex.Flowable
import javax.inject.Inject

class RefreshExpensesEventProviderImpl @Inject constructor(
    private val timestampProvider: IExpensesSyncTimestampProvider
) : ExpensesListFragmentViewModel.IRefreshExpensesListEventProvider {
    override fun getRefreshFlow(): Flowable<Unit> {
        return timestampProvider.get().map { }
    }
}