package com.upreality.car.auth.presentation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import javax.inject.Inject

@AndroidEntryPoint
class LoggedOutActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: IAuthNavigator

    @Inject
    lateinit var googleSignInNavigator: IGoogleSignInNavigator
    private val viewModel: AuthViewModel by viewModels()

    private val googleLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        googleSignInNavigator.processGoogleSignInResult(this, result, viewModel)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAuthState().firstElement().subscribe({ authState ->
            when (authState) {
                is AuthState.Authorized -> navigator.completeAuthorization(authState.account, this)
                AuthState.Unauthorized -> tryAutoLogin()
            }
        }) { error ->
            Log.e("Error", "Logged out error: $error")
        }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun tryAutoLogin() {
        viewModel.getLastAuthType().firstElement().subscribe({ authType ->
            when (authType) {
                AuthType.PHONE -> navigator.goToLogin(this) //TODO: implement
                AuthType.EMAIL -> navigator.goToLogin(this) //TODO: implement
                AuthType.GOOGLE -> launchGoogleSignIn()
                else -> navigator.goToLogin(this)
            }
        }) { error ->
            Log.e("Error", "Logged out error: $error")
        }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun launchGoogleSignIn() {
        googleSignInNavigator
            .getGoogleSignInActivityIntent(this, true)
            .let(googleLauncher::launch)
    }
}