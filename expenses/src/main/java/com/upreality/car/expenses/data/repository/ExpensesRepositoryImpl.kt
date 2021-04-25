package com.upreality.car.expenses.data.repository

import android.util.Log
import com.upreality.car.expenses.data.local.expenses.converters.RoomExpenseFilterConverter
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.expence.Expense
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val expensesLocalDataSource: IExpensesLocalDataSource,
    private val remoteDataSource: IExpensesRemoteDataSource
) : IExpensesRepository {

    override fun create(expense: Expense): Completable {
        return expense.let(remoteDataSource::create).doOnError {
            Log.d("error","")
        }
    }

    override fun get(filter: ExpenseFilter): Flowable<List<Expense>> {
        val localFilter = RoomExpenseFilterConverter.convert(filter)
        return expensesLocalDataSource.get(localFilter)
    }

    override fun update(expense: Expense): Completable {
        return expense.let(remoteDataSource::update)
    }

    override fun delete(expense: Expense): Completable {
        return expense.let(remoteDataSource::delete)
    }
}