package com.upreality.car.expenses.data.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ExpensesFirebaseModule {
    @Provides
    fun provideExpensesDBReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}