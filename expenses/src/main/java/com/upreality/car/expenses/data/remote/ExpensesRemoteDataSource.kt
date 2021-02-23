package com.upreality.car.expenses.data.remote

import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.database.*
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseEntity
import com.upreality.car.expenses.data.local.expensesinfo.dao.ExpenseInfoDAO
import com.upreality.car.expenses.data.local.expensesinfo.model.converters.ExpenseRemoteStateConverter
import com.upreality.car.expenses.data.local.expensesinfo.model.entities.ExpenseInfo
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoIdFilter
import com.upreality.car.expenses.data.remote.firestore.converters.RemoteExpenseConverter
import com.upreality.car.expenses.data.remote.firestore.converters.RemoteExpenseConverter.toExpenseDetails
import com.upreality.car.expenses.data.remote.firestore.dao.ExpenseDetailsFirestoreDAO
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseDetailsRemoteFilter
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class ExpensesRemoteDataSource @Inject constructor(
    private val expenseInfoDAO: ExpenseInfoDAO,
    private val remoteExpensesDAO: IExpensesRemoteDAO,
    private val remoteExpenseDetailsDAO: ExpenseDetailsFirestoreDAO
) {

    companion object {
        private const val EXPENSES_TABLE_NAME = "expenses"
    }

    fun delete(expense: ): Completable {
        return getRemoteInstance(expense).flatMapCompletable { remoteExpense ->
            val deleteExpense = remoteExpensesDAO.delete(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val deleteDetails = remoteExpenseDetailsDAO.delete(details)
            deleteExpense.andThen(deleteDetails)
        }
    }

    fun update(expense: Expense): Completable {
        return getRemoteInstance(expense).flatMapCompletable { remoteExpense ->
            val updateExpense = remoteExpensesDAO.update(remoteExpense)
            val details = toExpenseDetails(expense, remoteExpense.detailsId)
            val updateDetails = remoteExpenseDetailsDAO.update(details)
            updateExpense.andThen(updateDetails)
        }
    }

    private fun getExpenseInfo(expenseId: Long): Maybe<ExpenseInfo> {
        val infoSelector = ExpenseInfoIdFilter(expenseId)
        val query = SimpleSQLiteQuery(infoSelector.getFilterExpression())
        return expenseInfoDAO.load(query)
            .map(List<ExpenseInfo>::firstOrNull)
            .firstElement()
    }

    private fun getRemoteInstance(expense: Expense): Maybe<ExpenseEntityFirestore> {
        return getExpenseInfo(expense.id).flatMap { info ->
            val selector = ExpenseRemoteFilter.Id(info.remoteId)
            remoteExpensesDAO
                .get(selector)
                .firstElement()
                .map { it.first() }
        }
    }

    fun get(filter: ExpenseRemoteFilter): Flowable<List<Expense>> {
        return remoteExpensesDAO.get(filter).flatMapSingle(this::convertToExpenses)
    }

    private fun convertToExpenses(entities: List<ExpenseEntityFirestore>): Single<List<Expense>> {
        return Flowable.fromIterable(entities).flatMapMaybe { remoteEntity ->
            val detailsSelector = ExpenseDetailsRemoteFilter.Id(remoteEntity.detailsId)
            val detailsMaybe = remoteExpenseDetailsDAO
                .get(detailsSelector)
                .firstElement()
                .map(List<ExpenseDetailsFirestore>::firstOrNull)

            val expenseMaybe = detailsMaybe.map { remoteDetails ->
                RemoteExpenseConverter.toExpense(remoteEntity, remoteDetails)
            }
            expenseMaybe
        }.toList()
    }


    fun create(expense: Expense): Maybe<String> {
        val details = toExpenseDetails(expense, String())
        val createDetailsMaybe = remoteExpenseDetailsDAO.create(details)
        return createDetailsMaybe.flatMap { detailsId ->
            val expenseEntity = RemoteExpenseConverter.toExpenseEntity(expense, detailsId)
            remoteExpensesDAO.create(expenseEntity)
        }
    }
}