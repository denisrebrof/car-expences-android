package com.upreality.car.auth.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.auth.R
import com.upreality.car.auth.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val binding: ActivityAuthBinding by viewBinding(ActivityAuthBinding::bind)
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var googleSignInNavigator: IGoogleSignInNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        binding.testAuthButton.setOnClickListener(this::launchGoogleSignIn)
    }

    private val googleLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        googleSignInNavigator.processGoogleSignInResult(this, result, viewModel)
    }

    private fun launchGoogleSignIn(source: View) {
        googleSignInNavigator
            .getGoogleSignInActivityIntent(this, false)
            .let(googleLauncher::launch)
    }
}