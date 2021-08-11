package com.upreality.car.auth.presentation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import javax.inject.Inject

class GoogleSignInNavigator @Inject constructor(
    private val authNavigator: IAuthNavigator
) : IGoogleSignInNavigator {

    override fun getGoogleSignInActivityIntent(context: Context, tryRelogin: Boolean): Intent {
        val intent = Intent(context, GoogleSignInActivity::class.java)
        if(tryRelogin){
            intent.putExtra(GoogleSignInActivity.TRY_LAST_SIGNED_IN_ACCOUNT, true)
        }
        return intent
    }

    override fun processGoogleSignInResult(
        source: ComponentActivity,
        result: ActivityResult,
        viewModel: AuthViewModel
    ) {
        if (result.resultCode == GoogleSignInActivity.ResolveResult.SUCCESS.resultCode) {
            val authCode = result.data?.getStringExtra(GoogleSignInActivity.AUTH_CODE_EXTRA_KEY)
            authCode?.let(viewModel::googleSignIn)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ account ->
                    authNavigator.completeAuthorization(account, source)
                }) { _ ->
                    Toast.makeText(source, "auth failure", Toast.LENGTH_SHORT).show()
                }?.disposeBy(source.lifecycle.disposers.onStop)
        }
    }
}