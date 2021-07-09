package com.upreality.car.expenses.data.remote.expenseoperations.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenseoperations.model.entities.ExpenseRemoteOperation
import com.upreality.car.expenses.data.remote.expenseoperations.model.filters.ExpenseRemoteOperationFilter
import com.upreality.car.expenses.data.remote.expenses.converters.DateConverter
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

    fun delete(operation: ExpenseRemoteOperation): Completable {
        val docRef = operationsList.document(operation.id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(operation: ExpenseRemoteOperation): Completable {
        val docRef = operationsList.document(operation.id)
        return RxFirestore.setDocument(docRef, operation)
    }

    fun get(filter: ExpenseRemoteOperationFilter): Flowable<List<ExpenseRemoteOperation>> {
        return when (filter) {
            ExpenseRemoteOperationFilter.All -> getCollectionFlow()
            is ExpenseRemoteOperationFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
            is ExpenseRemoteOperationFilter.FromTime -> getCollectionFromTime(filter.time)
        }
    }

    fun create(expense: ExpenseRemoteOperation): Maybe<String> {
        val docRef = operationsList.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, expense)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseRemoteOperation>> {
        return RxFirestore.observeQueryRef(operationsList, ExpenseRemoteOperation::class.java)
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseRemoteOperation> {
        val doc = operationsList.document(id)
        return RxFirestore.observeDocumentRef(doc, ExpenseRemoteOperation::class.java)
    }

    private fun getCollectionFromTime(time: Long): Flowable<List<ExpenseRemoteOperation>> {
        val date = DateConverter.toDate(time)
        val fromTimeQuery =
            operationsList.whereGreaterThan(ExpenseRemoteOperation::timestamp.name, date)
        return RxFirestore.observeQueryRef(fromTimeQuery, ExpenseRemoteOperation::class.java)
    }
}