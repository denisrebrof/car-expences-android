package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
import com.upreality.car.expenses.data.local.expensesinfo.model.queries.ExpenseInfoAllFilter
import com.upreality.car.expenses.data.remote.ExpensesRemoteDataSource
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
    lateinit var remDS: ExpensesRemoteDataSource

    @Inject
    lateinit var remOpDAO: ExpenseOperationRemoteDAO

    @Inject
    lateinit var eiLocalDS: ExpensesInfoLocalDataSource

    private lateinit var binding: ActivityExpencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.createExpenseButton.setOnClickListener { executeCreation() }
        sync.createSyncLoop().disposeBy(lifecycle.disposers.onStop)
        repository.get(ExpenseFilter.All)
            .map(List<Expense>::size)
            .map(Int::toString)
            .observeOn(mainThread())
            .subscribe(this::setText)
            .disposeBy(lifecycle.disposers.onStop)

        remDS.get(ExpenseRemoteFilter.All)
            .map(List<ExpenseRemote>::size)
            .map(Int::toString)
            .observeOn(mainThread())
            .subscribe(this::setText2)
            .disposeBy(lifecycle.disposers.onStop)

        eiLocalDS.get(ExpenseInfoAllFilter).subscribe {
            Log.d("SYNC", "infos mdifies")
        }.disposeBy(lifecycle.disposers.onStop)

        val filter = ExpenseRemoteOperationFilter.FromTime(0)
        remOpDAO.get(filter)
            .observeOn(mainThread())
            .subscribe {
                binding.textOp.text = "Oper Size from zero: ${it.size}"
        }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun executeCreation() {
        val expense = Expense.Maintenance(Date(), 100F, MaintenanceType.Other, 2F)
        repository.create(expense)
            .subscribeOn(Schedulers.io())
            .subscribe()
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun setText(text: String) {
        binding.text.text = text
        Log.d("DB", "Create expense result: $text")
    }

    private fun setText2(text: String) {
        binding.text2.text = text
    }
}