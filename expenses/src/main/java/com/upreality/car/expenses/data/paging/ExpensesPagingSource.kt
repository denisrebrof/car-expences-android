package com.upreality.car.expenses.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExpensesPagingSource @Inject constructor(
    val repository: IExpensesRepository,
) : RxPagingSource<Int, Expense>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Expense>> {
        // Start refresh at page 1 if undefined.
        val nextPage = params.key ?: 1
        val response = ExpenseFilter.Paged(nextPage, params.loadSize).let(repository::get)

        return response
            .firstElement()
            .toSingle()
            .subscribeOn(Schedulers.io())
            .map { expensesList ->
            LoadResult.Page(
                data = expensesList,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (expensesList.isEmpty()) null else nextPage + 1
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Expense>): Int? {
        return state.anchorPosition
    }
}