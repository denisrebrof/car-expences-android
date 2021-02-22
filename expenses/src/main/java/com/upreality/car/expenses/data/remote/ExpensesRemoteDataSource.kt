package com.upreality.car.expenses.data.remote

import com.google.firebase.database.*
import com.upreality.car.expenses.data.local.expenses.converters.ExpenseConverter
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseDetails
import com.upreality.car.expenses.data.local.expenses.model.entities.ExpenseEntity
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.subjects.CompletableSubject
import javax.inject.Inject

class ExpensesRemoteDataSource @Inject constructor(
    dao: IExpensesRemoteDAO
) {

//    private val converter = ExpenseConverter()
//
//    companion object {
//        private const val EXPENSES_TABLE_NAME = "expenses"
//    }
//
//    fun delete(expense: Expense): Completable {
//        val completable = CompletableSubject.create()
//        dbRef.child(expense.id.toString()).removeValue { error, ref -> completable.onComplete() }
//        return completable
//    }
//
//    fun update(expense: Expense): Completable {
//        val completable = CompletableSubject.create()
//        //TODO process and check
//        dbRef.child(expense.id.toString())
//            .setValue(expense) { error, ref -> completable.onComplete() }
//        return completable
//    }
//
//    fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
//        val listMapper = DataSnapshotMapper.listOf(ExpenseEntity::class.java)
//        val expenseEntitiesFlow = RxFirestore.observeDocumentRef(expensesTable, listMapper)
//        val details = ExpenseDetails.ExpenseFinesDetails(0, FinesCategories.Other)//stub
//        val expensesFlow = expenseEntitiesFlow.map { expenseEntityList ->
//            expenseEntityList.map {
//                converter.toExpense(it, details)
//            }
//        }
//        return expensesFlow
//    }
//
//    fun create(expense: Expense): Maybe<String> {
//        val docRef = expensesTable.document()
//        val setValueCompletable = RxFirebaseDatabase.setValue(expensesTable)
//        val resultMaybe = Maybe.just(childId)
//        return setValueCompletable.andThen(resultMaybe)
//    }

}