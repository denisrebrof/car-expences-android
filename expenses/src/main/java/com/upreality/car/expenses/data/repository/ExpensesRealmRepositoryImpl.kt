package com.upreality.car.expenses.data.repository

import android.util.Log
import com.upreality.car.expenses.data.realm.model.ExpenseRealm
import com.upreality.car.expenses.data.realm.model.ExpenseRealmConverter
import com.upreality.car.expenses.data.realm.model.ExpenseRealmFields
import com.upreality.car.expenses.data.realm.model.ExpenseRealmTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import data.SyncedRealmProvider
import domain.RequestPagingState
import domain.RxListExtentions.mapList
import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.RealmQuery
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject

class ExpensesRealmRepositoryImpl @Inject constructor(
    private val realmProvider: SyncedRealmProvider
) : IExpensesRepository {

    override fun create(expense: Expense): Completable {
        expense.id = UUID.randomUUID().hashCode().toLong()
        return Completable.fromAction {
            val realm = realmProvider.getRealmInstance()
            realm.beginTransaction()
            try {
                val dataModel = ExpenseRealmConverter.fromDomain(expense)
                realm.insertOrUpdate(dataModel)
                realm.commitTransaction()
            } catch (exception: Exception) {
                Log.e("", "Error while create expense: $exception")
            }
            realm.close()
        }
    }

    private fun applyFilter(
        query: RealmQuery<ExpenseRealm>,
        filter: ExpenseFilter
    ): RealmQuery<ExpenseRealm> {
        val dateField = ExpenseRealmFields.DATE
        return when (filter) {
            ExpenseFilter.All -> query
            ExpenseFilter.Fines -> filterQueryByType(query, ExpenseType.Fines)
            ExpenseFilter.Fuel -> filterQueryByType(query, ExpenseType.Fuel)
            ExpenseFilter.Maintenance -> filterQueryByType(query, ExpenseType.Maintenance)
            is ExpenseFilter.Id -> query.equalTo(ExpenseRealmFields._ID, filter.id)
            is ExpenseFilter.DateRange -> query.between(dateField, filter.from, filter.to)
        }
    }

    override fun get(
        filters: List<ExpenseFilter>,
        pagingState: RequestPagingState
    ): Flowable<List<Expense>> {
        val realm = realmProvider.getRealmInstance()
        var expensesQuery = realm.where(ExpenseRealm::class.java)
        filters.forEach { expensesQuery = applyFilter(expensesQuery, it) }
        return expensesQuery.findAllAsync()
            .asFlowable()
            .filter(RealmResults<ExpenseRealm>::isLoaded)
            .map { results -> pageResults(results, pagingState) }
            .mapList(ExpenseRealmConverter::toDomain)
    }

    private fun <T> pageResults(results: RealmResults<T>, pagingState: RequestPagingState): List<T> {
        return when (pagingState) {
            is RequestPagingState.Paged -> pageList(
                results.toList(),
                pagingState.cursor.toInt().coerceAtLeast(0),
                pagingState.cursor.toInt() + pagingState.pageSize
            )
            else -> results.toList()
        }
    }

    private fun <T> pageList(list: List<T>, from: Int, to: Int): List<T> {
        return when {
            list.size >= to -> list.subList(from, to)
            list.size > from -> list.subList(from, list.size)
            else -> listOf()
        }
    }

    private fun filterQueryByType(
        query: RealmQuery<ExpenseRealm>,
        type: ExpenseType
    ): RealmQuery<ExpenseRealm> {
        return query.contains(
            ExpenseRealmFields.TYPE_ID,
            ExpenseRealmTypeConverter.toId(type).toString()
        )
    }

    override fun update(expense: Expense): Completable {
        return Completable.fromAction {
            val realm = realmProvider.getRealmInstance()
            realm.beginTransaction()
            try {
                val dataModel = ExpenseRealmConverter.fromDomain(expense)
                realm.insertOrUpdate(dataModel)
                realm.commitTransaction()
            } catch (exception: Exception) {
                Log.e("", "Error while update expense: $exception")
            }
            realm.close()
        }
    }

    override fun delete(expense: Expense): Completable {
        return Completable.fromAction {
            val realm = realmProvider.getRealmInstance()
            realm.beginTransaction()
            try {
                realm.where(ExpenseRealm::class.java)
                    .equalTo(ExpenseRealmFields._ID, expense.id)
                    .findFirst()
                    ?.deleteFromRealm()
                realm.commitTransaction()
            } catch (exception: Exception) {
                Log.e("", "Error while delete expense: $exception")
            }
            realm.close()
        }
    }
}