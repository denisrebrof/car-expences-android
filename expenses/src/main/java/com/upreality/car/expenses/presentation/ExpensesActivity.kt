package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.upreality.car.expenses.data.local.room.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.remote.ExpensesRemoteDAO
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationRemoteDAO
import com.upreality.car.expenses.data.remote.expenses.model.ExpenseRemote
import com.upreality.car.expenses.data.remote.expenses.model.filters.ExpenseRemoteFilter
import com.upreality.car.expenses.databinding.ActivityExpencesBinding
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.IExpensesSyncService
import com.upreality.car.expenses.domain.model.ExpenseFilter
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpensesActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: IExpensesRepository

    @Inject
    lateinit var sync: IExpensesSyncService

    @Inject
    lateinit var remDS: ExpensesRemoteDAO

    @Inject
    lateinit var remOpDAO: ExpenseOperationRemoteDAO

    @Inject
    lateinit var eiLocalDS: ExpensesInfoLocalDataSource

    private lateinit var binding: ActivityExpencesBinding

    private var cost = 100f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.createExpenseButton.setOnClickListener { executeCreation() }
        binding.deleteExpensesButton.setOnClickListener { executeDelete() }
        binding.modifyExpenseButton.setOnClickListener { executeUpdate() }
        sync.createSyncLoop()
            .doOnDispose {
                Log.e("ULError", "Dispose WTF???")
            }
            .doOnEvent {
                Log.e("ULError", "Event WTF???")
            }.doOnTerminate {
                Log.e("ULError", "Terminate")
            }.subscribe({
                Log.e("ULError", "Completed WTF???")
            }) {
                Log.e("ULError", it.toString())
            }.disposeBy(lifecycle.disposers.onStop)

        repository
            .get(ExpenseFilter.All.let(::listOf))
            .observeOn(mainThread())
            .doOnError {
                Log.e("Get", "Event WTF???")
            }.doOnTerminate {
                Log.e("Get", "Terminate")
            }
            .doOnCancel {
                Log.e("Get", "Cancel")
            }
            .doOnError {
                Log.e("Get", "Error")
            }
            .doOnComplete {
                Log.e("Get", "Event Complete???")
            }
            .subscribe(this::onExpensesUpdates) {
                Log.e("Get Error", it.toString())
            }
            .disposeBy(lifecycle.disposers.onStop)

        remDS
            .get(ExpenseRemoteFilter.All)
            .map(List<ExpenseRemote>::size)
            .map(Int::toString)
            .observeOn(mainThread())
            .subscribe(this::setText2) {
                Log.e("Get Remote Error", it.toString())
            }
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun executeDelete() {
        repository.get(ExpenseFilter.All.let(::listOf))
            .firstElement()
            .map { list -> listOf(list.first()) }
            .flattenAsFlowable { it }
            .flatMapCompletable(repository::delete)
            .subscribe({}) {
                Log.e("Delete Error", it.toString())
            }
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun executeUpdate() {
        repository.get(ExpenseFilter.All.let(::listOf))
            .firstElement()
            .map { list -> listOf(list.last()) }
            .flattenAsFlowable { it }
            .map(this::getIncreasedExpense)
            .flatMapCompletable(repository::update)
            .subscribe({}) {
                Log.e("Update Error", it.toString())
            }
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun getIncreasedExpense(expense: Expense): Expense {
        return when (expense) {
            is Expense.Fuel -> expense.copy(cost = 16f)
            is Expense.Maintenance -> expense.copy(cost = 16f)
            is Expense.Fine -> expense.copy(cost = 16f)
        }.apply { id = expense.id }
    }

    private fun executeCreation() {
        val expense = Expense.Maintenance(Date(), cost, MaintenanceType.Other, 2F)
        cost += 1
        repository.create(expense)
            .subscribeOn(Schedulers.io())
            .subscribe({}) {
                Log.e("Update Error", it.toString())
            }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun onExpensesUpdates(expenses: List<Expense>) {
        val last = expenses.lastOrNull()
        binding.lastExpenseCost.text = last?.let(Expense::cost)?.toString() ?: "Undefined"
        binding.lastExpenseDate.text = last?.let(Expense::date)?.toString() ?: "Undefined"
        binding.text.text = expenses.size.toString()
    }

    private fun setText2(text: String) {
        binding.text2.text = text
    }
}