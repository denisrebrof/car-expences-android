package com.upreality.stats.data.di

import com.upreality.stats.data.StatsBackendRepositoryImpl
import com.upreality.stats.data.StatsRepositoryImpl
import com.upreality.stats.domain.IStatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatsRepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRepository(repoImpl: StatsBackendRepositoryImpl): IStatsRepository
//    abstract fun provideRepository(repoImpl: StatsRepositoryImpl): IStatsRepository
}