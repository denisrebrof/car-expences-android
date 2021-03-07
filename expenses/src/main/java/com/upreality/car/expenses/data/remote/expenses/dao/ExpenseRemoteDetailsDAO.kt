package com.upreality.car.expenses.data.remote.expenses.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseRemoteDetailsEntity
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteDetailsFilter
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteDetailsFilter.All
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteDetailsFilter.Id
import durdinapps.rxfirebase2.RxFirestore
import durdinapps.rxfirebase2.RxFirestore.observeDocumentRef
import durdinapps.rxfirebase2.RxFirestore.observeQueryRef
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseRemoteDetailsDAO @Inject constructor(
    remoteStorage: FirebaseFirestore
) {

    private val expenseDetailsCollection = remoteStorage.collection(EXPENSE_DETAILS_COLLECTION)

    companion object {
        private const val EXPENSE_DETAILS_COLLECTION = "expense_details"
    }

    fun delete(details: ExpenseRemoteDetailsEntity): Completable {
        val docRef = expenseDetailsCollection.document(details.base_id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(details: ExpenseRemoteDetailsEntity): Completable {
        val docRef = expenseDetailsCollection.document(details.base_id)
        return RxFirestore.setDocument(docRef, details)
    }

    fun get(filter: ExpenseRemoteDetailsFilter): Flowable<List<ExpenseRemoteDetailsEntity>> {
        return when (filter) {
            is All -> observeQueryRef(expenseDetailsCollection, ExpenseRemoteDetailsEntity::class.java)
            is Id -> {
                val doc = expenseDetailsCollection.document(filter.id)
                observeDocumentRef(doc, ExpenseRemoteDetailsEntity::class.java).map { listOf(it) }
            }
        }
    }

    fun create(details: ExpenseRemoteDetailsEntity): Maybe<String> {
        val docRef = expenseDetailsCollection.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, details)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }
}