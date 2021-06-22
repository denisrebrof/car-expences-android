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
import javax.inject.Inject

class ExpensesRealmRepositoryImpl @Inject constructor() : IExpensesRepository {

    private val baseRealm: Realm = Realm.getDefaultInstance()

    override fun create(expense: Expense): Completable {
        val realm = Realm.getDefaultInstance()
        val dataModel = ExpenseRealmConverter.fromDomain(expense)
        return Completable.fromAction {
            realm.copyToRealmOrUpdate(dataModel)
        }.doOnDispose {
            realm.close()
        }
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val realm = Realm.getDefaultInstance()
        val expensesQuery = baseRealm.where(ExpenseRealm::class.java)
        return when (filter) {
            ExpenseFilter.All -> expensesQuery
            ExpenseFilter.Fines -> filterQueryByType(expensesQuery, ExpenseType.Fines)
            ExpenseFilter.Fuel -> filterQueryByType(expensesQuery, ExpenseType.Fuel)
            ExpenseFilter.Maintenance -> filterQueryByType(expensesQuery, ExpenseType.Maintenance)
            is ExpenseFilter.Paged -> expensesQuery.greaterThanOrEqualTo(
                ExpenseRealmFields.ID,
                filter.cursor
            ).limit(filter.pageSize.toLong())
        }.findAllAsync().asFlowable().map { results ->
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
        val realm = Realm.getDefaultInstance()
        val dataModel = ExpenseRealmConverter.fromDomain(expense)
        return Completable.fromAction {
            realm.copyToRealmOrUpdate(dataModel)
        }.doOnDispose {
            realm.close()
        }
    }

    override fun delete(expense: Expense): Completable {
        val realm = Realm.getDefaultInstance()
        return Completable.fromAction {
            realm.where(ExpenseRealm::class.java)
                .contains(ExpenseRealmFields.ID, expense.id.toString())
                .findFirst()
                ?.deleteFromRealm()
        }.doOnDispose {
            realm.close()
        }
    }
}