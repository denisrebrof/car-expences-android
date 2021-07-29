package com.upreality.car.expenses.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import domain.RequestPagingState
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
        val state = RequestPagingState.Paged(key.toLong(), params.loadSize)
        val response = repository.get(ExpenseFilter.All.let(::listOf), state)

        return response
            .firstElement()
            .toSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { expensesList ->
                var prevKey = if (key == 0) null else (key - params.loadSize).coerceAtLeast(0)
                if (prevKey == 0) {
                    prevKey = null
                }
                val nextKey =
                    if (expensesList.size < params.loadSize) null else key + params.loadSize
                LoadResult.Page(expensesList, prevKey, nextKey)
            }
    }

    override fun getRefreshKey(state: PagingState<Int, Expense>): Int? {
        return 0
    }
}