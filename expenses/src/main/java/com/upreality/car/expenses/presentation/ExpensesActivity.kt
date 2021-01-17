package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpensesActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: IExpensesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expences)
    }

    override fun onStart() {
        super.onStart()
        val btn = findViewById<Button>(R.id.create_expense_button)
        btn.setOnClickListener { executeCreation() }
    }

    private fun executeCreation() {
        val expense = Expense.Maintenance(Date(), 100F, MaintenanceType.Other, 2F)
        repository.create(expense)
            .subscribeOn(Schedulers.io())
            .observeOn(mainThread())
            .map { it.toString() }
            .subscribe(this::setText)
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun setText(text: String) {
        val textView = findViewById<MaterialTextView>(R.id.text)
        textView.text = text
        Log.d("DB", "Create expense result: $text")
    }
}