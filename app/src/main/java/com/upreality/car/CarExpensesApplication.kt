package com.upreality.car

import android.app.Application
import android.util.Log
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import io.realm.mongodb.App
import javax.inject.Inject

@HiltAndroidApp
class CarExpensesApplication : Application() {
    @Inject
    lateinit var realmApp: App

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("FCM", "token: $it")
        }
    }
}