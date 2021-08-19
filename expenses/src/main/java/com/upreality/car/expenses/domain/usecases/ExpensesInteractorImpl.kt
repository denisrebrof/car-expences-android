package com.upreality.car.expenses.domain.usecases

import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import domain.RequestPagingState
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

class ExpensesInteractorImpl @Inject constructor(
    private val repository: IExpensesRepository
) : IExpensesInteractor {

    override fun createExpense(expense: Expense) = repository.create(expense)

    override fun getExpensesFlow(
        filter: ExpenseFilter,
        pagingState: RequestPagingState
    ): Flowable<List<Expense>> {
        return getExpensesFlow(filter.let(::listOf))
    }

    override fun getExpensesFlow(
        filters: List<ExpenseFilter>,
        pagingState: RequestPagingState
    ): Flowable<List<Expense>> {
        return repository.get(filters)
    }

    override fun deleteExpense(expense: Expense) = repository.delete(expense)

    override fun updateExpense(expense: Expense) = repository.update(expense)

    override fun getExpenseMaybe(id: Long): Maybe<Expense> {
        return ExpenseFilter.Id(id)
            .let(this::getExpensesFlow)
            .map(List<Expense>::first)
            .firstElement()
    }

    override fun deleteExpense(id: Long): Completable {
        val stubExpense = Expense.Fuel(Date(), 0f).also { it.id = id }
        return repository.delete(stubExpense)
    }
}

