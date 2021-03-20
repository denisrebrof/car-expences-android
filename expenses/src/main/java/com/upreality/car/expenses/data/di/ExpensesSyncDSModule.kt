package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.sync.IExpensesSyncLocalDataSource
import com.upreality.car.expenses.data.sync.IExpensesSyncRemoteDataSource
import com.upreality.car.expenses.data.sync.datasources.ExpensesSyncLocalDataSourceImpl
import com.upreality.car.expenses.data.sync.datasources.ExpensesSyncRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesSyncDSModule {
    @Binds
    abstract fun bindLocal(syncDS: ExpensesSyncLocalDataSourceImpl): IExpensesSyncLocalDataSource

    @Binds
    abstract fun bindRemote(syncDS: ExpensesSyncRemoteDataSourceImpl): IExpensesSyncRemoteDataSource
}