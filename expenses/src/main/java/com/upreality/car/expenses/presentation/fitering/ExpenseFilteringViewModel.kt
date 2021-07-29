package com.upreality.car.expenses.presentation.fitering

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.DateTimeInteractor
import javax.inject.Inject

@HiltViewModel
class ExpenseFilteringViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dateTimeInteractor: DateTimeInteractor
) : ViewModel() {

}