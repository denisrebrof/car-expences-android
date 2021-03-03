package com.upreality.car.expenses.data.remote.expenseoperations.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseEntityFirestore
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseOperationFirestore
import com.upreality.car.expenses.data.remote.expenseoperations.model.filters.ExpenseOperationFilter
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseOperationFirestoreDAO @Inject constructor(
    remoteStorage: FirebaseFirestore
) {

    private val operationsList = remoteStorage.collection(EXPENSE_OPERATIONS_TABLE_NAME)

    companion object {
        private const val EXPENSE_OPERATIONS_TABLE_NAME = "expense_operations"
    }

    fun delete(operation: ExpenseOperationFirestore): Completable {
        val docRef = operationsList.document(operation.id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(operation: ExpenseOperationFirestore): Completable {
        val docRef = operationsList.document(operation.id)
        return RxFirestore.setDocument(docRef, operation)
    }

    fun get(filter: ExpenseOperationFilter): Flowable<List<ExpenseOperationFirestore>> {
        return when (filter) {
            ExpenseOperationFilter.All -> getCollectionFlow()
            is ExpenseOperationFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
            is ExpenseOperationFilter.FromTime -> getCollectionFromTime(filter.time)
        }
    }

    fun create(expense: ExpenseOperationFirestore): Maybe<String> {
        val docRef = operationsList.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, expense)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseOperationFirestore>> {
        return RxFirestore.observeQueryRef(operationsList, ExpenseOperationFirestore::class.java)
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseOperationFirestore> {
        val doc = operationsList.document(id)
        return RxFirestore.observeDocumentRef(doc, ExpenseOperationFirestore::class.java)
    }

    private fun getCollectionFromTime(time: Long): Flowable<List<ExpenseOperationFirestore>> {
        val fromTimeQuery = operationsList.whereGreaterThan(ExpenseOperationFirestore::timestamp.name, time)
        return RxFirestore.observeQueryRef(fromTimeQuery, ExpenseOperationFirestore::class.java)
    }
}