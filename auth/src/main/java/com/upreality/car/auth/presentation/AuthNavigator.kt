package com.upreality.car.auth.presentation

import android.content.Context
import android.widget.Toast
import com.upreality.car.auth.domain.Account
import javax.inject.Inject

class AuthNavigator @Inject constructor(

) {
    fun completeAuthorization(account: Account, context: Context) {
        Toast.makeText(context, "auth success", Toast.LENGTH_SHORT).show()
    }
}