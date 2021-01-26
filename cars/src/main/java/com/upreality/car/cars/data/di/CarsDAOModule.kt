package com.upreality.car.cars.data.di

import com.upreality.car.cars.data.database.CarsDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CarsDAOModule {
    @Provides
    fun provideExpenseDao(
        database: CarsDB
    ) = database.getCarsDAO()
}
