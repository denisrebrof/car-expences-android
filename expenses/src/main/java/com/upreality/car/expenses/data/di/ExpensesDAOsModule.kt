package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.database.ExpensesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object ExpensesDAOsModule {
    @Provides
    fun provideExpenseDao(
        database: ExpensesDB
    ) = database.getExpensesDAO()

    @Provides
    fun provideFuelDao(
        database: ExpensesDB
    ) = database.getFuelDAO()

    @Provides
    fun provideFinesDao(
        database: ExpensesDB
    ) = database.getFinesDAO()

    @Provides
    fun provideMaintenanceDao(
        database: ExpensesDB
    ) = database.getMaintenanceDAO()
}