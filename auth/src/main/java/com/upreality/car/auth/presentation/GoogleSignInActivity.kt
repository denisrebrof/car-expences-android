package com.upreality.car.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.upreality.car.auth.domain.IAuthRepository
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class GoogleSignInActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 1
        const val TOKEN_EXTRA_KEY = "GOOGLE_TOKEN"
        const val TRY_LAST_SIGNED_IN_ACCOUNT = "TRY_LAST_SIGNED_IN_ACCOUNT"
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var authRepository: IAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInClient = GoogleSignIn.getClient(this, authRepository.getGoogleSignInOptions())
        val tryRefresh = savedInstanceState?.containsKey(TRY_LAST_SIGNED_IN_ACCOUNT) == true
        if (tryRefresh) {
            GoogleSignIn.getLastSignedInAccount(this)?.let { account ->
                resolveActivity(ResolveResult.SUCCESS, account.idToken)
                return
            }
        }
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task?.addOnSuccessListener {
                resolveActivity(ResolveResult.SUCCESS, it.idToken)
            }?.addOnCanceledListener {
                resolveActivity(ResolveResult.CANCELLED)
            }?.addOnFailureListener {
                resolveActivity(ResolveResult.FAILURE)
            }
        }
    }

//    private fun googleSignInOnBackend(account: GoogleSignInAccount) {
//        account.idToken
//            ?.let(authRepository::googleSignIn)
//            ?.subscribeOn(Schedulers.io())
//            ?.observeOn(AndroidSchedulers.mainThread())
//            ?.subscribe({
//                Log.d("success", "res: $it")
//                Toast.makeText(this, "Auth success", Toast.LENGTH_SHORT).show()
//            }) {
//                Log.e("error", "error: $it")
//                Toast.makeText(this, "Auth failure", Toast.LENGTH_SHORT).show()
//            }?.let(disposable::add)
//    }

    private fun resolveActivity(result: ResolveResult, idToken: String? = null) {
        val success = result==ResolveResult.SUCCESS && idToken!=null
        val data = if(success) Intent().putExtra(TOKEN_EXTRA_KEY, idToken) else null
        setResult(result.resultCode, data)
        finish()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    enum class ResolveResult(val resultCode: Int) {
        SUCCESS(0),
        CANCELLED(1),
        FAILURE(2),
    }
}
