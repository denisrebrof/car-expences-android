package com.upreality.car.expenses.data.remote.expenses.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseFirestoreFilter
import durdinapps.rxfirebase2.RxFirestore
import durdinapps.rxfirebase2.RxFirestore.observeQueryRef
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseEntityFirestoreDAO @Inject constructor(
    remoteStorage: FirebaseFirestore
) {

    private val expensesCollection = remoteStorage.collection(EXPENSES_TABLE_NAME)

    companion object {
        private const val EXPENSES_TABLE_NAME = "expenses"
    }

    fun delete(expense: ExpenseEntityFirestore): Completable {
        val docRef = expensesCollection.document(expense.id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(expense: ExpenseEntityFirestore): Completable {
        val docRef = expensesCollection.document(expense.id)
        return RxFirestore.setDocument(docRef, expense)
    }

    fun get(filter: ExpenseFirestoreFilter): Flowable<List<ExpenseEntityFirestore>> {
        return when (filter) {
            ExpenseFirestoreFilter.All -> getCollectionFlow()
            is ExpenseFirestoreFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
        }
    }

    fun create(expense: ExpenseEntityFirestore): Maybe<String> {
        val docRef = expensesCollection.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, expense)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseEntityFirestore>> {
        return observeQueryRef(expensesCollection, ExpenseEntityFirestore::class.java)
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseEntityFirestore> {
        val doc = expensesCollection.document(id)
        return RxFirestore.observeDocumentRef(doc, ExpenseEntityFirestore::class.java)
    }
}