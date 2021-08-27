package com.upreality.stats.data.di

import com.upreality.stats.data.StatsBackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StatsApiModule {
    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): StatsBackendApi {
        return retrofit.create(StatsBackendApi::class.java)
    }
}