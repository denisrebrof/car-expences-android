package com.upreality.car.expenses.domain.di

import com.upreality.car.expenses.domain.usecases.ExpensesInteractorImpl
import com.upreality.car.expenses.domain.usecases.IExpensesInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ActivityComponent::class, ViewModelComponent::class)
abstract class ExpensesInteractorModule {
    @Binds
    abstract fun provideInteractor(
        interactorImpl: ExpensesInteractorImpl
    ): IExpensesInteractor
}