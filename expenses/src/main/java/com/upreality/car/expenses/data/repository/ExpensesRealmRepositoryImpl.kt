package com.upreality.car.expenses.data.repository

import com.upreality.car.expenses.data.realm.model.ExpenseRealm
import com.upreality.car.expenses.data.realm.model.ExpenseRealmConverter
import com.upreality.car.expenses.data.realm.model.ExpenseRealmFields
import com.upreality.car.expenses.data.realm.model.ExpenseRealmTypeConverter
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject

class ExpensesRealmRepositoryImpl @Inject constructor() : IExpensesRepository {

    override fun create(expense: Expense): Completable {
        expense.id = UUID.randomUUID().hashCode().toLong()
        return Completable.fromAction {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            val dataModel = ExpenseRealmConverter.fromDomain(expense, realm)
            realm.copyToRealmOrUpdate(dataModel)
            realm.commitTransaction()
            realm.close()
        }
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val realm = Realm.getDefaultInstance()
        val expensesQuery = realm.where(ExpenseRealm::class.java)
        return when (filter) {
            ExpenseFilter.All -> expensesQuery
            ExpenseFilter.Fines -> filterQueryByType(expensesQuery, ExpenseType.Fines)
            ExpenseFilter.Fuel -> filterQueryByType(expensesQuery, ExpenseType.Fuel)
            ExpenseFilter.Maintenance -> filterQueryByType(expensesQuery, ExpenseType.Maintenance)
            is ExpenseFilter.Paged -> expensesQuery.
                .limit(filter.pageSize.toLong())
        }.findAllAsync()
            .asFlowable()
            .filter(RealmResults<ExpenseRealm>::isLoaded)
            .map { results ->
                results.toList().map(ExpenseRealmConverter::toDomain)
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
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            val dataModel = ExpenseRealmConverter.fromDomain(expense, realm)
            realm.copyToRealmOrUpdate(dataModel)
            realm.commitTransaction()
            realm.close()
        }
    }

    override fun delete(expense: Expense): Completable {
        return Completable.fromAction {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.where(ExpenseRealm::class.java)
                .contains(ExpenseRealmFields.ID, expense.id.toString())
                .findFirst()
                ?.deleteFromRealm()
            realm.commitTransaction()
            realm.close()
        }
    }
}