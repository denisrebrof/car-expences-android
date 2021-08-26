package com.upreality.car.auth.data.di

import android.content.Context
import com.upreality.car.auth.data.local.TokenDAO
import com.upreality.car.auth.data.remote.api.AuthAPI
import com.upreality.car.auth.data.remote.api.TestGetIdApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthApiModule {
    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): AuthAPI {
        return retrofit.create(AuthAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideTestApi(retrofit: Retrofit): TestGetIdApi {
        return retrofit.create(TestGetIdApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTokenDAO(@ApplicationContext context: Context): TokenDAO {
        return TokenDAO(context)
    }
}