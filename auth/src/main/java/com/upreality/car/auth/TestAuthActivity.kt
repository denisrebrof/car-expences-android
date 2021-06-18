package com.upreality.car.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.upreality.car.auth.databinding.ActivityTestAuthBinding

@Suppress("DEPRECATION")
class TestAuthActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 1
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityTestAuthBinding by viewBinding(ActivityTestAuthBinding::bind)

    private var loggedInState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_auth)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateState(account)

        binding.testAuthButton.setOnClickListener { onClick() }
    }

    private fun updateState(account: GoogleSignInAccount? = null) {
        loggedInState = account != null
        binding.testAuthStatus.text = if (loggedInState) account!!.displayName else "You are not logged!"
        binding.testAuthButton.text = if (loggedInState) "Log OUT" else "Google Log In"
    }

    private fun onClick() {
        if (loggedInState) {
            googleSignInClient.signOut().addOnSuccessListener {
                updateState()
            }
        } else {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
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

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        task?.addOnSuccessListener {
            updateState(it)
        }?.addOnCanceledListener {
            Log.i("","Cancel")
        }?.addOnFailureListener {
            Log.i("","Cancel")
        }
    }
}