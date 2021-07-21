package com.upreality.car.expenses.presentation

import android.content.Context
import android.content.Intent
import javax.inject.Inject

class ExpenseEditingNavigator @Inject constructor() {

    companion object {
        const val EXPENSE_ID = "EXPENSE_ID"
    }

    fun openEditing(context: Context, expenseId: Long) {
        val intent = Intent(context, ExpenseEditingActivity::class.java)
        intent.putExtra(EXPENSE_ID, expenseId)
        context.startActivity(intent)
    }

    fun openCreation(context: Context) {
        val intent = Intent(context, ExpenseEditingActivity::class.java)
        context.startActivity(intent)
    }
}