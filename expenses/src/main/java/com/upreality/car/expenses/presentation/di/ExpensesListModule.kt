package com.upreality.car.expenses.presentation.di

import android.content.Context
import com.upreality.car.expenses.presentation.ExpensesListAdapter
import com.upreality.car.expenses.presentation.ExpensesListAdapterExpenseTypeDataProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ExpensesListModule {
    @Provides
    @ActivityScoped
    fun provide(@ActivityContext context: Context): ExpensesListAdapter.ExpenseTypeDataProvider {
        return ExpensesListAdapterExpenseTypeDataProviderImpl(context)
    }
}