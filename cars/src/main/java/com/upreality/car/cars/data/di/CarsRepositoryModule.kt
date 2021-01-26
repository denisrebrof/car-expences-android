package com.upreality.car.cars.data.di

import com.upreality.car.cars.data.model.CarsRepositoryImpl
import com.upreality.car.cars.domain.ICarsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CarsRepositoryModule {
    @Binds
    abstract fun provideRepository(
        repoImpl: CarsRepositoryImpl
    ): ICarsRepository
}