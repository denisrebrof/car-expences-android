package com.upreality.car.auth.presentation

import android.content.Context
import com.upreality.car.auth.domain.Account

interface IAuthNavigator {
    fun completeAuthorization(account: Account, context: Context)
    fun goToLogin(context: Context)
}