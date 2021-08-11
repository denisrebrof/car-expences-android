package com.upreality.car.expenses.data.sync

import io.reactivex.Completable
import io.reactivex.Flowable

interface IExpensesSyncTimestampProvider {
    fun get(): Flowable<Long>
    fun set(timestamp: Long): Completable
}