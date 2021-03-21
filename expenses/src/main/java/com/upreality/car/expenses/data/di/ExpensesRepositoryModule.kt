package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.repository.ExpensesRepositoryImpl
import com.upreality.car.expenses.data.repository.IExpensesLocalDataSource
import com.upreality.car.expenses.data.sync.datasources.ExpensesLocalDSSyncDecorator
import com.upreality.car.expenses.domain.IExpensesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesRepositoryModule {
    @Binds
    abstract fun provideRepository(repoImpl: ExpensesRepositoryImpl): IExpensesRepository

    @Binds
    abstract fun provideLocalDS(ds: ExpensesLocalDSSyncDecorator): IExpensesLocalDataSource
}