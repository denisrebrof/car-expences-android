package com.upreality.car.expenses.presentation

import io.reactivex.Flowable

interface IExpenseEditingViewModel {
    fun getViewStateFlow(): Flowable<ExpenseEditingViewState>
    fun getCancellationEventFlow(): Flowable<Unit>
    fun executeIntent(intent: ExpenseEditingIntent)
}