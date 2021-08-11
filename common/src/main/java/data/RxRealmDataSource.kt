package data//package com.upreality.car.common.data
//
//import android.util.Log
//import domain.RxListExtentions.mapList
//import io.reactivex.Completable
//import io.reactivex.Flowable
//import io.realm.RealmObject
//import io.realm.RealmResults
//import javax.inject.Inject
//
//abstract class RxRealmDataSource<REALM_MODEL : RealmObject, DOMAIN_MODEL : Any> @Inject constructor(
//    private val realmProvider: SyncedRealmProvider
//) {
//
//    abstract val converter: Converter<REALM_MODEL, DOMAIN_MODEL>
//
//    private fun logCRUDException(exception: Exception, operation: String) {
//        Log.e("RxRealm", "Error while $operation: $exception")
//    }
//
//    fun create(model: DOMAIN_MODEL): Completable {
//        return Completable.fromAction {
//            val realm = realmProvider.getRealmInstance()
//            realm.beginTransaction()
//            try {
//                val dataModel = converter.fromDomain(model)
//                realm.insertOrUpdate(dataModel)
//                realm.commitTransaction()
//            } catch (exception: Exception) {
//                logCRUDException(exception, "create")
//            }
//            realm.close()
//        }
//    }
//
//    fun get(filter: ExpenseFilter): Flowable<List<DOMAIN_MODEL>> {
//        val realm = realmProvider.getRealmInstance()
//        val expensesQuery = realm.where(REALM_MODEL::class.java)
//        return when (filter) {
//            ExpenseFilter.All -> expensesQuery
//            ExpenseFilter.Fines -> filterQueryByType(expensesQuery, ExpenseType.Fines)
//            ExpenseFilter.Fuel -> filterQueryByType(expensesQuery, ExpenseType.Fuel)
//            ExpenseFilter.Maintenance -> filterQueryByType(expensesQuery, ExpenseType.Maintenance)
//            is ExpenseFilter.Paged -> expensesQuery
//            is ExpenseFilter.Id -> expensesQuery.equalTo(ExpenseRealmFields._ID, filter.id)
//        }.findAllAsync()
//            .asFlowable()
//            .filter(RealmResults<ExpenseRealm>::isLoaded)
//            .map { results ->
//                val resultsList = when (filter) {
//                    is ExpenseFilter.Paged -> pageList(
//                        results.toList(),
//                        filter.cursor.toInt().coerceAtLeast(0),
//                        filter.cursor.toInt() + filter.pageSize
//                    )
//                    else -> results.toList()
//                }
//                resultsList
//            }.mapList(ExpenseRealmConverter::toDomain)
//    }
//
//    abstract class Converter<REALM_MODEL : RealmObject, DOMAIN_MODEL> {
//        abstract fun fromDomain(domainModel: DOMAIN_MODEL): REALM_MODEL
//        abstract fun toDomain(realmModel: REALM_MODEL): DOMAIN_MODEL
//    }
//}