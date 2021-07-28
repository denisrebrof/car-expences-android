package com.upreality.car.expenses.data.repository

import android.util.Log
import com.upreality.car.common.data.SyncedRealmProvider
import com.upreality.car.expenses.data.realm.model.ExpenseRealm
import com.upreality.car.expenses.data.realm.model.ExpenseRealmConverter
import com.upreality.car.expenses.data.realm.model.ExpenseRealmFields
import com.upreality.car.expenses.data.realm.model.ExpenseRealmTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
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

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val realm = realmProvider.getRealmInstance()
        val expensesQuery = realm.where(ExpenseRealm::class.java)
        return when (filter) {
            ExpenseFilter.All -> expensesQuery
            ExpenseFilter.Fines -> filterQueryByType(expensesQuery, ExpenseType.Fines)
            ExpenseFilter.Fuel -> filterQueryByType(expensesQuery, ExpenseType.Fuel)
            ExpenseFilter.Maintenance -> filterQueryByType(expensesQuery, ExpenseType.Maintenance)
            is ExpenseFilter.Paged -> expensesQuery
            is ExpenseFilter.Id -> expensesQuery.equalTo(ExpenseRealmFields._ID, filter.id)
            is ExpenseFilter.DateRange -> expensesQuery.between(ExpenseRealmFields.DATE, filter.from, filter.to)
        }.findAllAsync()
            .asFlowable()
            .filter(RealmResults<ExpenseRealm>::isLoaded)
            .map { results ->
                val resultsList = when (filter) {
                    is ExpenseFilter.Paged -> pageList(
                        results.toList(),
                        filter.cursor.toInt().coerceAtLeast(0),
                        filter.cursor.toInt() + filter.pageSize
                    )
                    else -> results.toList()
                }
                resultsList
            }.mapList(ExpenseRealmConverter::toDomain)
    }

    private fun <T> pageList(list: List<T>, from: Int, to: Int): List<T> {
        val pagedList = when {
            list.size >= to -> list.subList(from, to)
            list.size > from -> list.subList(from, list.size)
            else -> listOf()
        }
        return pagedList
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