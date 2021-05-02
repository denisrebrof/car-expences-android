package com.upreality.car.expenses.domain

import io.reactivex.Completable

interface IExpensesSyncService {
    fun createSyncLoop(): Completable
}