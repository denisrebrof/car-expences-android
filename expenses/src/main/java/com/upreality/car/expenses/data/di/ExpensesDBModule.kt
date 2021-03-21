package com.upreality.car.expenses.data.di

import android.content.Context
import androidx.room.Room
import com.upreality.car.expenses.data.local.database.ExpensesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesDBModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context
    ): ExpensesDB {
        return Room.databaseBuilder(app, ExpensesDB::class.java, "expenses_db").build()
    }
}