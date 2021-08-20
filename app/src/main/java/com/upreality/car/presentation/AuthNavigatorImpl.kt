package com.upreality.car.presentation

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.presentation.AuthActivity
import com.upreality.car.auth.presentation.IAuthNavigator
import javax.inject.Inject

class AuthNavigatorImpl @Inject constructor(
    private val analytics: FirebaseAnalytics
) : IAuthNavigator {

    override fun completeAuthorization(account: Account, context: Context) {
        Intent(context, MainActivity::class.java).let(context::startActivity)
        val params = bundleOf()
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
    }

    override fun goToLogin(context: Context) {
        Intent(context, AuthActivity::class.java).let(context::startActivity)
    }
}