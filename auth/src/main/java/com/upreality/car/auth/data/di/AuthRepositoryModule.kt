package com.upreality.car.auth.data.di

import android.content.Context
import com.upreality.car.auth.data.AuthRepositoryImpl
import com.upreality.car.auth.data.IAuthLocalDataSource
import com.upreality.car.auth.data.IAuthRemoteDataSource
import com.upreality.car.auth.data.local.AuthLocalDataSourceImpl
import com.upreality.car.auth.data.local.ILastAuthStateDAO
import com.upreality.car.auth.data.local.LastAuthStateDAOImpl
import com.upreality.car.auth.data.remote.AuthRemoteDataSourceImpl
import com.upreality.car.auth.data.remote.AuthRemoteDataSourceRealmImpl
import com.upreality.car.auth.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRepository(repoImpl: AuthRepositoryImpl): IAuthRepository

    @Binds
    @Singleton
    abstract fun provideLocalDS(ds: AuthLocalDataSourceImpl): IAuthLocalDataSource

    @Binds
    @Singleton
    abstract fun provideRemoteDS(ds: AuthRemoteDataSourceImpl): IAuthRemoteDataSource

//    @Binds
//    @Singleton
//    abstract fun provideRemoteDS(ds: AuthRemoteDataSourceRealmImpl): IAuthRemoteDataSource
}