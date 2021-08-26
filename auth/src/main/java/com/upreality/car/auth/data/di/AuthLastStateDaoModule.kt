package com.upreality.car.auth.data.di

import android.content.Context
import com.upreality.car.auth.data.local.ILastAuthStateDAO
import com.upreality.car.auth.data.local.LastAuthStateDAOImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthLastStateDaoModule {
    @Singleton
    @Provides
    fun provideLastAuthStateDAO(@ApplicationContext context: Context): ILastAuthStateDAO {
        return LastAuthStateDAOImpl(context)
    }
}