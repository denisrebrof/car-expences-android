package com.upreality.car.expenses.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.flowable
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.domain.usecases.ExpensesInteractorImpl
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ExpenseListModel
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ExpenseListModel.ExpenseModel
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ExpenseListModel.SyncIndicator
import com.upreality.car.expenses.presentation.paging.ExpensesPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.SyncInteractor
import io.reactivex.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpensesListFragmentViewModel @Inject constructor(
    //TODO fix injection
    private val interactor: ExpensesInteractorImpl,
    private val sourceFactory: IExpensesPagingSourceFactory,
    //TODO fix injection
    private val refreshEventProvider: RefreshExpensesRealmEventProvider
) : ViewModel() {

    private var lastSource: ExpensesPagingSource? = null

    private val calendar = Calendar.getInstance()

    @ExperimentalCoroutinesApi
    fun getExpensesFlow(): Flowable<PagingData<ExpenseListModel>> {
        val config = PagingConfig(pageSize = 6, initialLoadSize = 6)
        val pagingDataFlow = Pager(config) {
            sourceFactory.get().also { lastSource = it }
        }.flowable.cachedIn(viewModelScope).map(this::mapPagingData)
        val syncPagingData = (SyncIndicator(0) as ExpenseListModel)
            .let(::listOf)
            .let(PagingData.Companion::from)
        return pagingDataFlow.startWith(syncPagingData)
    }

    fun getRefreshFlow(): Flowable<Unit> {
        return refreshEventProvider.getRefreshFlow().doOnNext {
            refresh()
        }
    }

    fun setFilters(filters: List<ExpenseFilter>) {
        lastSource?.invalidate()
        lastSource?.filters = filters
    }

    fun refresh() = lastSource?.invalidate()

    fun deleteExpense(expense: Expense) = interactor.deleteExpense(expense)

    private fun mapPagingData(data: PagingData<Expense>): PagingData<ExpenseListModel> {
        val pagingData: PagingData<ExpenseListModel> = data.map(ExpenseListModel::ExpenseModel)
        return pagingData.insertSeparators { prev: ExpenseListModel?, next: ExpenseListModel? ->
            val nextDate = next.requestExpenseDate().getOrNull() ?: return@insertSeparators null
            val nextDay = calendar.apply { time = nextDate }.get(Calendar.DAY_OF_YEAR)

            val prevDate = prev.requestExpenseDate().getOrNull()
            val prevDay = prevDate?.also(calendar::setTime)?.let {
                calendar.get(Calendar.DAY_OF_YEAR)
            }

            return@insertSeparators when (prevDay) {
                nextDay -> null
                else -> ExpenseListModel.DateSeparator(nextDate)
            }
        }
    }

    private fun ExpenseListModel?.requestExpenseDate() = kotlin.runCatching {
        (this as ExpenseModel).expense.date
    }

    interface IExpensesPagingSourceFactory {
        fun get(): ExpensesPagingSource
    }

    interface IRefreshExpensesListEventProvider {
        fun getRefreshFlow(): Flowable<Unit>
    }
}