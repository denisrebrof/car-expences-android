package com.upreality.car.expenses.data.remote.firestore.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseDetailsRemoteFilter
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseDetailsRemoteFilter.All
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseDetailsRemoteFilter.Id
import durdinapps.rxfirebase2.RxFirestore
import durdinapps.rxfirebase2.RxFirestore.observeDocumentRef
import durdinapps.rxfirebase2.RxFirestore.observeQueryRef
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseDetailsFirestoreDAO @Inject constructor(
    remoteStorage: FirebaseFirestore
) {

    private val expenseDetailsCollection = remoteStorage.collection(EXPENSE_DETAILS_COLLECTION)

    companion object {
        private const val EXPENSE_DETAILS_COLLECTION = "expense_details"
    }

    fun delete(details: ExpenseDetailsFirestore): Completable {
        val docRef = expenseDetailsCollection.document(details.base_id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(details: ExpenseDetailsFirestore): Completable {
        val docRef = expenseDetailsCollection.document(details.base_id)
        return RxFirestore.setDocument(docRef, details)
    }

    fun get(filter: ExpenseDetailsRemoteFilter): Flowable<List<ExpenseDetailsFirestore>> {
        return when (filter) {
            is All -> observeQueryRef(expenseDetailsCollection, ExpenseDetailsFirestore::class.java)
            is Id -> {
                val doc = expenseDetailsCollection.document(filter.id)
                observeDocumentRef(doc, ExpenseDetailsFirestore::class.java).map { listOf(it) }
            }
        }
    }

    fun create(details: ExpenseDetailsFirestore): Maybe<String> {
        val docRef = expenseDetailsCollection.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, details)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }
}