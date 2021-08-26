package com.upreality.car.auth.data.di

import com.google.gson.Gson
import com.upreality.car.auth.data.AccessTokenInterceptor
import com.upreality.car.auth.data.TokenAuthenticator
import com.upreality.car.auth.data.remote.api.TokenRefreshApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val baseUrl = "https://car-expenses-dev.herokuapp.com"
    private const val localBaseUrl = "http://10.0.2.2:4000"

    @Singleton
    @Provides
    fun provideRetrofit(
        authenticator: TokenAuthenticator,
        interceptor: AccessTokenInterceptor
    ): Retrofit {
        val gson = Gson()

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(localBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(TokenRefreshApi::class.java).let(authenticator::setTokenApi)

        return retrofit
    }
}