package com.upreality.car.expenses.data.sync.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LocalInfoSchedulerProviderImpl @Inject constructor(): ILocalInfoSchedulerProvider {

    private val scheduler = Schedulers.single()

    override fun get(): Scheduler {
        return scheduler
    }
}