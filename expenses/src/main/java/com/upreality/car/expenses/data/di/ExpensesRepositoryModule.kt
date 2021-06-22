package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.local.room.ExpensesLocalDataSourceImpl
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSourceImpl
import com.upreality.car.expenses.data.repository.*
import com.upreality.car.expenses.domain.IExpensesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesRepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRepository(repoImpl: ExpensesRepositoryImpl): IExpensesRepository

    @Binds
    @Singleton
    abstract fun provideLocalDS(ds: ExpensesLocalDataSourceImpl): IExpensesLocalDataSource

    @Binds
    @Singleton
    abstract fun provideRemoteDS(ds: ExpensesRemoteDataSourceImpl): IExpensesRemoteDataSource
}