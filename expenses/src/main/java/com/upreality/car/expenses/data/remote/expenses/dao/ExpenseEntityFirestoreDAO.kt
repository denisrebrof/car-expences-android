package com.upreality.car.expenses.data.remote.expenses.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
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

    fun delete(expense: ExpenseEntityRemote): Completable {
        val docRef = expensesCollection.document(expense.id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(expense: ExpenseEntityRemote): Completable {
        val docRef = expensesCollection.document(expense.id)
        return RxFirestore.setDocument(docRef, expense)
    }

    fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseEntityRemote>> {
        return when (filter) {
            ExpenseRemoteFilter.All -> getCollectionFlow()
            is ExpenseRemoteFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
        }
    }

    fun create(expense: ExpenseEntityRemote): Maybe<String> {
        val docRef = expensesCollection.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, expense)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseEntityRemote>> {
        return observeQueryRef(expensesCollection, ExpenseEntityRemote::class.java).doOnNext {
            Log.d("","")
        }.doOnError {
            Log.d("","")
        }
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseEntityRemote> {
        val doc = expensesCollection.document(id)
        return RxFirestore.observeDocumentRef(doc, ExpenseEntityRemote::class.java)
    }
}