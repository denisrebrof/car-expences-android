package com.upreality.car.expenses.data.sync.expenseoperations.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.sync.expenseoperations.model.entities.ExpenseOperationRemote
import com.upreality.car.expenses.data.sync.expenseoperations.model.filters.ExpenseRemoteOperationFilter
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseOperationRemoteDAO @Inject constructor(
    remoteStorage: FirebaseFirestore
) {

    private val operationsList = remoteStorage.collection(EXPENSE_OPERATIONS_TABLE_NAME)

    companion object {
        private const val EXPENSE_OPERATIONS_TABLE_NAME = "expense_operations"
    }

    fun delete(operation: ExpenseOperationRemote): Completable {
        val docRef = operationsList.document(operation.id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(operation: ExpenseOperationRemote): Completable {
        val docRef = operationsList.document(operation.id)
        return RxFirestore.setDocument(docRef, operation)
    }

    fun get(filter: ExpenseRemoteOperationFilter): Flowable<List<ExpenseOperationRemote>> {
        return when (filter) {
            ExpenseRemoteOperationFilter.All -> getCollectionFlow()
            is ExpenseRemoteOperationFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
            is ExpenseRemoteOperationFilter.FromTime -> getCollectionFromTime(filter.time)
        }
    }

    fun create(operation: ExpenseOperationRemote): Maybe<String> {
        val docRef = operationsList.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, operation)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseOperationRemote>> {
        return RxFirestore.observeQueryRef(operationsList, ExpenseOperationRemote::class.java)
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseOperationRemote> {
        val doc = operationsList.document(id)
        return RxFirestore.observeDocumentRef(doc, ExpenseOperationRemote::class.java)
    }

    private fun getCollectionFromTime(time: Long): Flowable<List<ExpenseOperationRemote>> {
        val fromTimeQuery = operationsList.whereGreaterThan(ExpenseOperationRemote::timestamp.name, time)
        return RxFirestore.observeQueryRef(fromTimeQuery, ExpenseOperationRemote::class.java)
    }
}