package com.upreality.car.auth.presentation

import androidx.lifecycle.ViewModel
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthType
import com.upreality.car.auth.domain.AuthUseCases
import com.upreality.car.auth.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCases: AuthUseCases
) : ViewModel() {
    fun googleSignIn(authCode: String) = useCases.googleSignIn(authCode)
    fun getLastAuthType(): Flowable<AuthType> = useCases.getLastAuthType()
    fun getAuthState(): Flowable<AuthState> = useCases.getAuthState()
}