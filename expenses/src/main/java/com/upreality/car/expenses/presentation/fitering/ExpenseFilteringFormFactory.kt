package com.upreality.car.expenses.presentation.fitering

import domain.DateTimeInteractor
import presentation.InputForm
import javax.inject.Inject

class ExpenseFilteringFormFactory @Inject constructor(
    private val dateTimeInteractor: DateTimeInteractor
) {
    fun create(): InputForm {
        return InputForm().apply {
            createField(
                ExpenseFilteringKeys.Type,
                InputForm.Companion::validateNotNull,

            )
            createField(
                ExpenseFilteringKeys.DateRange,
                InputForm.Companion::validateNotNull
            )
        }
    }
}