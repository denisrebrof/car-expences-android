package com.upreality.car

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.upreality.car.databinding.ActivityMainBinding
import com.upreality.car.expenses.domain.IExpensesSyncService
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

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
    }
}