package com.upreality.car.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.upreality.car.auth.databinding.ActivityAuthBinding
import com.upreality.car.auth.presentation.GoogleSignInActivity.ResolveResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var navigator: AuthNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityAuthBinding
            .inflate(layoutInflater)
            .also(this::binding::set)
            .let(ActivityAuthBinding::getRoot)
            .let(this::setContentView)

        binding.testAuthButton.setOnClickListener(this::launchGoogleSignIn)
    }

    private val googleLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == ResolveResult.SUCCESS.resultCode) {
            val token = result.data?.getStringExtra(GoogleSignInActivity.TOKEN_EXTRA_KEY)
            token?.let(viewModel::googleSignIn)?.subscribe({ account ->
                navigator.completeAuthorization(account, this)
            }) { _ ->
                Toast.makeText(this, "auth failure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchGoogleSignIn(source: View) {
        val intent = Intent(this, GoogleSignInActivity::class.java)
        googleLauncher.launch(intent)
    }
}