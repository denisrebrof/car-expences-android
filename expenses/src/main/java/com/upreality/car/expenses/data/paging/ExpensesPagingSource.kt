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
        val key = params.key ?: 0
        val response = ExpenseFilter.Paged(key.toLong(), params.loadSize).let(repository::get)

        return response
            .firstElement()
            .toSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { expensesList ->
                val prevKey = if (key == 0) null else (key - params.loadSize).coerceAtLeast(0)
                val nextKey = if (expensesList.size < params.loadSize) null else key + params.loadSize
                LoadResult.Page(expensesList, prevKey, nextKey)
            }
    }

    override fun getRefreshKey(state: PagingState<Int, Expense>): Int? {
        return state.anchorPosition
    }
}