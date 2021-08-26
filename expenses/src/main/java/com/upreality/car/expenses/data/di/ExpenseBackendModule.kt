package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.backend.ExpensesBackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpenseBackendModule {

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): ExpensesBackendApi {
        return retrofit.create(ExpensesBackendApi::class.java)
    }
}