package com.upreality.car.expenses.domain.usecases

import com.upreality.car.expenses.domain.IExpensesRepository
import javax.inject.Inject

class ExpensesInteractor @Inject constructor(
    private val repository: IExpensesRepository
) {

}