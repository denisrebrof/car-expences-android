package com.upreality.car.expenses.domain

import io.reactivex.disposables.Disposable

interface IExpensesSyncService {
    fun createSyncLoop(): Disposable
    fun triggerSync()
}