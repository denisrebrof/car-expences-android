package com.upreality.car.di

import com.upreality.car.auth.presentation.IAuthNavigator
import com.upreality.car.presentation.AuthNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthNavigatorModule {

    @Binds
    abstract fun provideNavigator(navigator: AuthNavigatorImpl): IAuthNavigator
}