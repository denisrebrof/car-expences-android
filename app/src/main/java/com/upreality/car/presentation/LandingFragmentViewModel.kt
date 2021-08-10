package com.upreality.car.presentation

import androidx.lifecycle.ViewModel
import com.upreality.car.auth.domain.Account
import com.upreality.car.auth.domain.AuthState
import com.upreality.car.auth.domain.AuthUseCases
import com.upreality.car.presentation.LandingFragmentActions.LoggedOut
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.subscribeWithLogError
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

@HiltViewModel
class LandingFragmentViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val viewStateProcessor = BehaviorProcessor.create<LandingFragmentViewState>()
    private val actionsProcessor = PublishProcessor.create<LandingFragmentActions>()

    fun getActionsFlow(): Flowable<LandingFragmentActions> = actionsProcessor
    fun getViewStateFlow(): Flowable<LandingFragmentViewState> = viewStateProcessor

    init {
        authUseCases
            .getAuthState()
            .distinctUntilChanged()
            .subscribeWithLogError { state ->
                when (state) {
                    is AuthState.Authorized -> onAccountUpdated(state.account)
                    AuthState.Unauthorized -> LoggedOut.let(actionsProcessor::onNext)
                }
            }.let(composite::add)
    }

    fun execute(intent: LandingFragmentIntents) {
        when (intent) {
            LandingFragmentIntents.LogOut -> logOut()
        }
    }

    private fun onAccountUpdated(account: Account) {
        viewStateProcessor.value?.copy(
            userName = account.id
        )?.let(viewStateProcessor::onNext)
    }

    private fun logOut() {
        authUseCases.logOut().subscribeWithLogError {
            actionsProcessor.onNext(LoggedOut)
        }.let(composite::add)
    }

    override fun onCleared() {
        super.onCleared()
        composite.clear()
    }
}