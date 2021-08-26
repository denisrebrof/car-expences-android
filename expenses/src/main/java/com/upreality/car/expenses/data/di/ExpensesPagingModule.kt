package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.presentation.paging.ExpensesPagingSourceFactoryImpl
import com.upreality.car.expenses.presentation.list.ExpensesListFragmentViewModel.IExpensesPagingSourceFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesPagingModule {
    @Binds
    @Singleton
    abstract fun bind(factory: ExpensesPagingSourceFactoryImpl): IExpensesPagingSourceFactory
}