package com.upreality.car.expenses.data.di

import com.upreality.car.expenses.data.sync.schedulers.ILocalInfoSchedulerProvider
import com.upreality.car.expenses.data.sync.schedulers.LocalInfoSchedulerProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalInfoSchedulerProviderModule {
    @Binds
    @Singleton
    abstract fun provideInstance(provider: LocalInfoSchedulerProviderImpl): ILocalInfoSchedulerProvider
}