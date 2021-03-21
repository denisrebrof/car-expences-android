package com.upreality.car.expenses.data.di

import android.content.Context
import com.upreality.car.expenses.data.sync.IExpensesSyncTimestampProvider
import com.upreality.car.expenses.data.sync.datasources.ExpensesSyncTimestampProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent ::class)
class ExpensesTimestampProviderModule {
    @Provides
    @Singleton
    fun bindService(@ApplicationContext context: Context): IExpensesSyncTimestampProvider{
        return ExpensesSyncTimestampProvider(context)
    }
}