package com.upreality.car.auth.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult

interface IGoogleSignInNavigator {
    fun processGoogleSignInResult(
        source: ComponentActivity,
        result: ActivityResult,
        viewModel: AuthViewModel
    )

    fun getGoogleSignInActivityIntent(context: Context, tryRelogin: Boolean): Intent
}