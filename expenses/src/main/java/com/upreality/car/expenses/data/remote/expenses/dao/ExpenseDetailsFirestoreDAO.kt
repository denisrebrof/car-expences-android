package com.upreality.car.expenses.data.remote.expenses.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.upreality.car.expenses.data.remote.expenses.model.entities.ExpenseDetailsRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseDetailsRemoteFilter
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseDetailsRemoteFilter.All
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseDetailsRemoteFilter.Id
import com.upreality.car.expenses.data.shared.model.ExpenseType
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

    fun delete(details: ExpenseDetailsRemote): Completable {
        val docRef = expenseDetailsCollection.document(details.base_id)
        return RxFirestore.deleteDocument(docRef)
    }

    fun update(details: ExpenseDetailsRemote): Completable {
        val docRef = expenseDetailsCollection.document(details.base_id)
        return RxFirestore.setDocument(docRef, details)
    }

    fun get(filter: ExpenseDetailsRemoteFilter): Flowable<List<ExpenseDetailsRemote>> {
        val type = (filter as? Id)?.type
        val deserialiseType = when (type) {
            ExpenseType.Fines -> ExpenseDetailsRemote.ExpenseFinesDetails::class.java
            ExpenseType.Fuel -> ExpenseDetailsRemote.ExpenseFuelDetails::class.java
            ExpenseType.Maintenance -> ExpenseDetailsRemote.ExpenseMaintenanceDetails::class.java
            null -> ExpenseDetailsRemote::class.java
        }
        return when (filter) {
            is All -> observeQueryRef(expenseDetailsCollection, ExpenseDetailsRemote::class.java)
            is Id -> {
                val doc = expenseDetailsCollection.document(filter.id)
                observeDocumentRef(doc, deserialiseType).map { listOf(it) }
            }
        }
    }

    fun create(details: ExpenseDetailsRemote): Maybe<String> {
        val docRef = expenseDetailsCollection.document()
        val setValueCompletable = RxFirestore.setDocument(docRef, details)
        val resultMaybe = Maybe.just(docRef.id)
        return setValueCompletable.andThen(resultMaybe)
    }
}