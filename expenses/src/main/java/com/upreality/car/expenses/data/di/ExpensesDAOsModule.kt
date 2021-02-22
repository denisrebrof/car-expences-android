package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.local.database.ExpensesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
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