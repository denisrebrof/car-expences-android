package com.upreality.car.expenses.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.IExpensesRepository
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
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
        val textView = findViewById<MaterialTextView>(R.id.text)
        val expense = Expense.Maintenance(Date(), 100F, MaintenanceType.Other, 2F)
        textView.text = repository.create(expense).toString()
    }
}