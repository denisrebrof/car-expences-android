package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.local.ExpensesLocalDataSource
import com.upreality.car.expenses.data.sync.ExpenseLocalDataSourceSaveInfoDecorator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpenseLocalDataSourceModule {
    @Binds
    abstract fun provideDataSource(
        decorator: ExpenseLocalDataSourceSaveInfoDecorator
    ): ExpensesLocalDataSource
}