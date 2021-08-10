package com.upreality.car.presentation

sealed class LandingFragmentIntents {
    object LogOut : LandingFragmentIntents()
}

sealed class LandingFragmentActions {
    object LoggedOut : LandingFragmentActions()
}

data class LandingFragmentViewState(
    val userName: String
)
