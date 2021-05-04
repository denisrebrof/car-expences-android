package com.upreality.car.expenses.data.paging

import androidx.paging.PagingSource
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.expence.Expense

class ExpensesPagingSource(
    val useCases: IExpensesRepository,
) : PagingSource<Int, Expense>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Expense> {
        // Start refresh at page 1 if undefined.
        val nextPage = params.key ?: 1
        val response = repository.get()

        return LoadResult.Page(
            data = response.movies,
            prevKey = if (nextPage == 1) null else nextPage - 1,
            nextKey = response.page + 1
        )
    }
}