package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.local.database.ExpensesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpenseInfoDaoModule {
    @Provides
    @Singleton
    fun provideExpenseInfoDao(
        database: ExpensesDB
    ) = database.getExpenseInfoDAO()
}