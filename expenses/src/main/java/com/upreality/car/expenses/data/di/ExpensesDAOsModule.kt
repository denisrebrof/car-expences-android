package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.sync.room.database.ExpensesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesDAOsModule {
    @Provides
    @Singleton
    fun provideExpenseDao(
        database: ExpensesDB
    ) = database.getExpensesDAO()

    @Provides
    @Singleton
    fun provideFuelDao(
        database: ExpensesDB
    ) = database.getFuelDAO()

    @Provides
    @Singleton
    fun provideFinesDao(
        database: ExpensesDB
    ) = database.getFinesDAO()

    @Provides
    @Singleton
    fun provideMaintenanceDao(
        database: ExpensesDB
    ) = database.getMaintenanceDAO()
}