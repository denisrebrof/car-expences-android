package com.upreality.car.expenses.data.sync.schedulers

import io.reactivex.Scheduler

interface ILocalInfoSchedulerProvider {
    fun get(): Scheduler
}