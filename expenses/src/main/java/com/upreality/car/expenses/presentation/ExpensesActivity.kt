package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.upreality.car.expenses.data.local.expensesinfo.ExpensesInfoLocalDataSource
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

    private lateinit var binding: ActivityExpencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

    }

}