package com.upreality.car.expenses.data.remote.firestore.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.IExpensesRemoteDAO
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseDetailsFirestore
import com.upreality.car.expenses.data.remote.firestore.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.firestore.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.domain.model.ExpenseFilter
import durdinapps.rxfirebase2.RxFirestore
import durdinapps.rxfirebase2.RxFirestore.observeQueryRef
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpensesFirestoreDAO @Inject constructor(
    remoteStorage: FirebaseFirestore
) : IExpensesRemoteDAO {

    private val expensesCollection = remoteStorage.collection(EXPENSES_TABLE_NAME)

    companion object {
        private const val EXPENSES_TABLE_NAME = "expenses"
    }

    override fun delete(expense: ExpenseEntityFirestore): Completable {
        val docRef = expensesCollection.document(expense.id)
        return RxFirestore.deleteDocument(docRef)
    }

    override fun update(expense: ExpenseEntityFirestore): Completable {
        val docRef = expensesCollection.document(expense.id)
        return RxFirestore.setDocument(docRef, expense)
    }

    override fun get(filter: ExpenseRemoteFilter): Flowable<List<ExpenseEntityFirestore>> {
        return when (filter) {
            ExpenseRemoteFilter.All -> getCollectionFlow()
            is ExpenseRemoteFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
        }
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseEntityFirestore>> {
        return observeQueryRef(expensesCollection, ExpenseEntityFirestore::class.java)
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseEntityFirestore> {
        val doc = expensesCollection.document(id)
        return RxFirestore.observeDocumentRef(doc, ExpenseEntityFirestore::class.java)
    }

    override fun create(expense: ExpenseEntityFirestore): Maybe<String> {
        val docRef = expensesCollection.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, expense)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }
}