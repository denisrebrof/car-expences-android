package com.upreality.car

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.realm.mongodb.App
import javax.inject.Inject

@HiltAndroidApp
class CarExpensesApplication : Application() {
    @Inject
    lateinit var realmApp: App
}