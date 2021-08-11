package com.upreality.car.brending.data.di

import com.upreality.car.brending.data.CarMarksRepoStub
import com.upreality.car.brending.domain.ICarMarkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CarRepositoryModule {
    @Binds
    abstract fun provideRepository(
        repoImpl: CarMarksRepoStub
    ): ICarMarkRepository
}