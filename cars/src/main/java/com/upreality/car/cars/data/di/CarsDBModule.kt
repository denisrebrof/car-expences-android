package com.upreality.car.cars.data.di

import android.content.Context
import androidx.room.Room
import com.upreality.car.cars.data.database.CarsDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CarsDBModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ): CarsDB {
        return Room.databaseBuilder(app, CarsDB::class.java, "cars_db").build()
    }
}