package com.upreality.car.expenses.data.remote.expensestate.dao

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenses.converters.DateConverter
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteState
import com.upreality.car.expenses.data.remote.expensestate.model.ExpenseRemoteStateFilter
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject

class ExpenseStateRemoteDAO @Inject constructor(
    userDocument: DocumentReference
) {

    private val statesList = userDocument.collection(EXPENSE_STATES_TABLE_NAME)

    companion object {
        private const val EXPENSE_STATES_TABLE_NAME = "expense_states"
    }

    fun delete(state: ExpenseRemoteState): Completable {
        val docRef = statesList.document(state.id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(operation: ExpenseRemoteState): Completable {
        val docRef = statesList.document(operation.id)
        return RxFirestore.setDocument(docRef, operation)
    }

    fun get(filter: ExpenseRemoteStateFilter): Flowable<List<ExpenseRemoteState>> {
        return when (filter) {
            ExpenseRemoteStateFilter.All -> getCollectionFlow()
            is ExpenseRemoteStateFilter.Id -> getDocumentFlow(filter.id).map { listOf(it) }
            is ExpenseRemoteStateFilter.FromTime -> getCollectionFromTime(filter.time)
            is ExpenseRemoteStateFilter.FromTimePersisted -> getCollectionFromTimePersisted(filter.time)
            is ExpenseRemoteStateFilter.ByRemoteId -> getDocumentByRemoteId(filter.remoteId)
        }
    }

    fun create(expense: ExpenseRemoteState): Maybe<String> {
        val docRef = statesList.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, expense)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }

    private fun getCollectionFlow(): Flowable<List<ExpenseRemoteState>> {
        return RxFirestore.observeQueryRef(statesList).map { snapshot ->
            snapshot.documents.map { document -> document.toObject(ExpenseRemoteState::class.java)!! }
        }
    }

    private fun getDocumentFlow(id: String): Flowable<ExpenseRemoteState> {
        val doc = statesList.document(id)
        return RxFirestore.observeDocumentRef(doc).map { snapshot ->
            snapshot.toObject(ExpenseRemoteState::class.java)
        }
    }

    private fun getCollectionFromTime(time: Long): Flowable<List<ExpenseRemoteState>> {
        val date = DateConverter.toDate(time)
        val fromTimeQuery = statesList.whereGreaterThan(ExpenseRemoteState::timestamp.name, date)
        return RxFirestore.observeQueryRef(fromTimeQuery).map { snapshot ->
            snapshot.documents.map { document -> document.toObject(ExpenseRemoteState::class.java)!! }
        }
    }

    private fun getCollectionFromTimePersisted(time: Long): Flowable<List<ExpenseRemoteState>> {
        val date = DateConverter.toDate(time)
        val fromTimeQuery = statesList
            .whereGreaterThan(ExpenseRemoteState::timestamp.name, date)
            .whereEqualTo(ExpenseRemoteState::deleted.name, false)
        return RxFirestore.observeQueryRef(fromTimeQuery).map { snapshot ->
            snapshot.documents.map { document -> document.toObject(ExpenseRemoteState::class.java)!! }
        }
    }

    private fun getDocumentByRemoteId(id: String): Flowable<List<ExpenseRemoteState>> {
        val byIdQuery = statesList.whereEqualTo(ExpenseRemoteState::remoteId.name, id)
        return RxFirestore.observeQueryRef(byIdQuery).map { snapshot ->
            snapshot.documents.map { document -> document.toObject(ExpenseRemoteState::class.java)!! }
        }
    }
}