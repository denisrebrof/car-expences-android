package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
import com.upreality.car.expenses.data.sync.ExpenseRemoteDataSourceSaveOperationDecorator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpenseRemoteDataSourceModule {
    @Binds
    abstract fun provideDataSource(
        decorator: ExpenseRemoteDataSourceSaveOperationDecorator
    ): ExpensesRemoteDataSource
}