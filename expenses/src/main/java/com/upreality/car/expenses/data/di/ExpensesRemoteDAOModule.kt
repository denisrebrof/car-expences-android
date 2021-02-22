package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.remote.firestore.dao.ExpensesFirestoreDAO
import com.upreality.car.expenses.data.remote.IExpensesRemoteDAO
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesRemoteDAOModule {
    @Binds
    abstract fun provideRemoteDAO(dao: ExpensesFirestoreDAO): IExpensesRemoteDAO
}