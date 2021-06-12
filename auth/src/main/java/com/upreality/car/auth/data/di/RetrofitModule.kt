package com.upreality.car.auth.data.di

import com.google.gson.Gson
import com.upreality.car.auth.data.remote.AuthAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val baseUrl = "http://10.0.2.2:4000"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val  gson = Gson()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): AuthAPI {
        return retrofit.create(AuthAPI::class.java)
    }
}