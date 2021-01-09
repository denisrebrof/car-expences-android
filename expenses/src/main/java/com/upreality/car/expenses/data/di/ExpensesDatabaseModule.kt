package com.upreality.car.expenses.data.di

import android.content.Context
import androidx.room.Room
import com.upreality.car.expenses.data.database.ExpensesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object ExpensesDatabaseModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ): ExpensesDB {
        return Room.databaseBuilder(app, ExpensesDB::class.java, "expenses_db").build()
    }
}