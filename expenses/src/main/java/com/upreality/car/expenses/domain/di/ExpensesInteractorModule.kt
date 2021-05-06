package com.upreality.car.expenses.domain.di

import com.upreality.car.expenses.domain.usecases.ExpensesInteractorImpl
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ExpensesInteractorModule {
    @Binds
    abstract fun provideInteractor(
        interactorImpl: ExpensesInteractorImpl
    ): IExpensesInteractor
}