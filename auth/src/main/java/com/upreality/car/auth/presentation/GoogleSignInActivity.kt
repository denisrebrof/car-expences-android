package com.upreality.car.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.upreality.car.auth.databinding.ActivityTestAuthBinding
import com.upreality.car.auth.domain.IAuthRepository
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class GoogleSignInActivity : AppCompatActivity() {
    companion object {
        const val RC_SIGN_IN = 1
        const val SIGN_IN_CODE_SUCCESS = 2
        const val SIGN_IN_CODE_FAILURE = 3
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var authRepository: IAuthRepository

    private lateinit var binding: ActivityTestAuthBinding

    private var loggedInState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        googleSignInClient = GoogleSignIn.getClient(this, authRepository.getGoogleSignInOptions())

        binding.testAuthButton.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            updateState(account)
            if (loggedInState) {
                googleSignInClient.signOut().addOnFailureListener {
                    Log.e("Sign out error", it.toString())
                    Toast.makeText(this, "Sign out error", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {
                    Log.d("SignOut","Sign out success")
                    updateState()
                    Toast.makeText(this, "Sign out success", Toast.LENGTH_SHORT).show()
                }
            } else {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateState(account)
        if (loggedInState) {
            updateState(account)
            googleSignInOnBackend(account!!)
        } else {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun updateState(account: GoogleSignInAccount? = null) {
        loggedInState = account != null
        binding.testAuthStatus.text =
            if (loggedInState) account!!.displayName else "You are not logged!"
        binding.testAuthButton.text = if (loggedInState) "Log OUT" else "Google Log In"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun googleSignInOnBackend(account: GoogleSignInAccount) {
        account.idToken
            ?.let(authRepository::googleSignIn)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                Log.d("success", "res: $it")
                Toast.makeText(this, "Auth success", Toast.LENGTH_SHORT).show()
            }) {
                Log.e("error", "error: $it")
                Toast.makeText(this, "Auth failure", Toast.LENGTH_SHORT).show()
            }?.let(disposable::add)
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        task?.addOnSuccessListener {
            updateState(it)
            googleSignInOnBackend(it)
        }?.addOnCanceledListener {
            Log.i("", "Cancel")
        }?.addOnFailureListener {
            Log.i("", "Failure")
        }
    }
}