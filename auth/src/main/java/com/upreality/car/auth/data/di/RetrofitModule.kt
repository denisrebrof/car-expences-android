package com.upreality.car.auth.data.di

import android.content.Context
import com.google.gson.Gson
import com.upreality.car.auth.data.AccessTokenInterceptor
import com.upreality.car.auth.data.local.ILastAuthStateDAO
import com.upreality.car.auth.data.local.LastAuthStateDAOImpl
import com.upreality.car.auth.data.local.TokenDAO
import com.upreality.car.auth.data.TokenAuthenticator
import com.upreality.car.auth.data.local.AccountDAO
import com.upreality.car.auth.data.remote.api.AuthAPI
import com.upreality.car.auth.data.remote.api.TestGetIdApi
import com.upreality.car.auth.data.remote.api.TokenRefreshApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofit.create(TokenRefreshApi::class.java).let(authenticator::setTokenApi)

        return retrofit
    }

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

    @Singleton
    @Provides
    fun provideAccountDAO(@ApplicationContext context: Context): AccountDAO {
        return AccountDAO(context)
    }

    @Singleton
    @Provides
    fun provideLastAuthStateDAO(@ApplicationContext context: Context): ILastAuthStateDAO {
        return LastAuthStateDAOImpl(context)
    }
}