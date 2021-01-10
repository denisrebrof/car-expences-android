package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.ExpensesRepository
import com.upreality.car.expenses.domain.IExpensesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class ExpensesRepositoryModule {
    @Binds
    abstract fun provideRepository(
        repoImpl: ExpensesRepository
    ): IExpensesRepository
}