package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.sync.ExpensesSyncServiceImpl
import com.upreality.car.expenses.domain.IExpensesSyncService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesSyncServiceModule {
    @Binds
    abstract fun bindService(syncDS: ExpensesSyncServiceImpl): IExpensesSyncService
}