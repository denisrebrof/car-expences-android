package com.upreality.car.presentation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.presentation.IAuthNavigator
import javax.inject.Inject

class AuthNavigatorImpl @Inject constructor(

) : IAuthNavigator {
    override fun completeAuthorization(account: Account, context: Context) {
        Toast.makeText(context, "auth success", Toast.LENGTH_SHORT).show()
        Intent(context, MainActivity::class.java).let(context::startActivity)
    }
}