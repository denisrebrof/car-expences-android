package com.upreality.car.auth.presentation

import androidx.lifecycle.ViewModel
import com.upreality.car.auth.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: IAuthRepository
) : ViewModel() {
    fun googleSignIn(token: String) = repository.googleSignIn(token)
}