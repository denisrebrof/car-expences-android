package com.upreality.car.expenses.data.backend

import com.upreality.car.expenses.data.backend.model.ExpenseBackendConverter
import com.upreality.car.expenses.data.backend.model.ExpenseFilterBackendConverter
import com.upreality.car.expenses.data.backend.model.ExpensesBackendRequest
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import domain.RequestPagingState
import domain.RxListExtentions.mapList
import javax.inject.Inject

class ExpensesBackendRepository @Inject constructor(
    private val api: ExpensesBackendApi
) : IExpensesRepository {

    override fun create(expense: Expense) =
        ExpenseBackendConverter.fromExpense(expense).let(api::create)

    override fun get(
        filters: List<ExpenseFilter>,
        pagingState: RequestPagingState
    ) = ExpensesBackendRequest(
        pagingState is RequestPagingState.Paged,
        (pagingState as? RequestPagingState.Paged)?.cursor ?: 0L,
        (pagingState as? RequestPagingState.Paged)?.pageSize?.toLong() ?: 0L,
        filters.map(ExpenseFilterBackendConverter::toId)
    ).let(api::get).mapList(ExpenseBackendConverter::toExpense)

    override fun update(expense: Expense) =
        ExpenseBackendConverter.fromExpense(expense).let(api::update)

    override fun delete(expense: Expense) = api.delete(expense.id)
}