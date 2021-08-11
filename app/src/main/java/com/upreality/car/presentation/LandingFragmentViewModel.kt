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
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import com.upreality.car.presentation.LandingFragmentViewState as ViewState

@HiltViewModel
class LandingFragmentViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val composite = CompositeDisposable()

    private val defaultViewState by lazy {
        ViewState("")
    }

    private val viewStateProcessor = BehaviorProcessor.createDefault(defaultViewState)
    private val actionsProcessor = PublishProcessor.create<LandingFragmentActions>()

    fun getActionsFlow(): Flowable<LandingFragmentActions> = actionsProcessor
    fun getViewStateFlow(): Flowable<ViewState> = viewStateProcessor

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
            LandingFragmentIntents.LogOut -> authUseCases
                .logOut()
                .subscribeOn(Schedulers.io())
                .subscribeWithLogError()
                .let(composite::add)
        }
    }

    private fun onAccountUpdated(account: Account) {
        viewStateProcessor.value?.copy(
            userName = account.id
        )?.let(viewStateProcessor::onNext)
    }

    override fun onCleared() {
        super.onCleared()
        composite.clear()
    }
}