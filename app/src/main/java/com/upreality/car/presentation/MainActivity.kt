package com.upreality.car.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.upreality.car.R
import com.upreality.car.databinding.ActivityMainBinding
import com.upreality.car.expenses.domain.IExpensesSyncService
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var analytics: FirebaseAnalytics

    //TODO: move to global scope
    @Inject
    lateinit var sync: IExpensesSyncService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navController = (navHost as NavHostFragment).navController
        binding.bottomNavigation.setupWithNavController(navController)

        //TODO: move to global scope
        sync.createSyncLoop()
            .subscribeWithLogError()
            .disposeBy(lifecycle.disposers.onDestroy)

        analytics = Firebase.analytics
    }
}