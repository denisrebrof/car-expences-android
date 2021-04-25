package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.remote.ExpensesRemoteDAO
import com.upreality.car.expenses.data.remote.expenseoperations.dao.ExpenseOperationRemoteDAO
import com.upreality.car.expenses.data.remote.expenseoperations.model.filters.ExpenseRemoteOperationFilter
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
        sync.createSyncLoop().disposeBy(lifecycle.disposers.onStop)

        val expensesFlow = repository
            .get(ExpenseFilter.All)
            .observeOn(mainThread())

        expensesFlow.subscribe(this::onExpensesUpdates)
            .disposeBy(lifecycle.disposers.onStop)

        remDS.get(ExpenseRemoteFilter.All)
            .map(List<ExpenseRemote>::size)
            .map(Int::toString)
            .observeOn(mainThread())
            .subscribe(this::setText2)
            .disposeBy(lifecycle.disposers.onStop)

        val filter = ExpenseRemoteOperationFilter.FromTime(0)
        remOpDAO.get(filter)
            .observeOn(mainThread())
            .subscribe {
                binding.textOp.text = "Oper Size from zero: ${it.size}"
            }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun executeDelete() {
        repository.get(ExpenseFilter.All)
            .firstElement()
            .map { list -> listOf(list.first()) }
            .flattenAsFlowable { it }
            .flatMapCompletable(repository::delete)
            .subscribe()
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun executeUpdate() {
        repository.get(ExpenseFilter.All)
            .firstElement()
            .map { list -> listOf(list.first()) }
            .flattenAsFlowable { it }
            .map(this::getIncreasedExpense)
            .flatMapCompletable(repository::update)
            .doOnError {
                Log.d("Error", "$it")
            }
            .subscribe()
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun getIncreasedExpense(expense: Expense): Expense {
        val updated = when (expense) {
            is Expense.Fuel -> expense.copy(cost = 16f)
            is Expense.Maintenance -> expense.copy(cost = 16f)
            is Expense.Fine -> expense.copy(cost = 16f)
        }.apply { id = expense.id }
        return updated
    }

    private fun executeCreation() {
        val expense = Expense.Maintenance(Date(), cost, MaintenanceType.Other, 2F)
        cost += 1
        repository.create(expense)
            .subscribeOn(Schedulers.io())
            .doOnError {
                Log.d("error","")
            }
            .subscribe()
            .disposeBy(lifecycle.disposers.onStop)
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