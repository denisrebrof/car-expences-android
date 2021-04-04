package com.upreality.car.expenses.data.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesFirebaseModule {
    @Provides
    @Singleton
    fun provideExpensesDBReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRootDocReference(
        firestore: FirebaseFirestore
    ): DocumentReference {
        return firestore.collection("user")
            .document("defaultUser")
    }
}