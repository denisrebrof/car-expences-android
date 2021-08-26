package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.backend.ExpensesBackendRepository
import com.upreality.car.expenses.domain.IExpensesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesRepositoryModule {
//    @Binds
//    @Singleton
//    abstract fun provideRepository(repoImpl: ExpensesRepositoryImpl): IExpensesRepository

//    @Binds
//    @Singleton
//    abstract fun provideLocalDS(ds: ExpensesLocalDataSourceImpl): IExpensesSyncLocalDataSource
//
//    @Binds
//    @Singleton
//    abstract fun provideRemoteDS(ds: ExpensesRemoteDataSourceImpl): IExpensesSyncRemoteDataSource

//    @Binds
//    @Singleton
//    abstract fun provideRepository(repoImpl: ExpensesRealmRepositoryImpl): IExpensesRepository

    @Binds
    @Singleton
    abstract fun provideRepository(repoImpl: ExpensesBackendRepository): IExpensesRepository
}