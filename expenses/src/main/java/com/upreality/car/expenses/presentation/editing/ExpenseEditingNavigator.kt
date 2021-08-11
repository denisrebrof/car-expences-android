package com.upreality.car.expenses.presentation.editing

import android.content.Context
import android.content.Intent
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.presentation.editing.ui.ExpenseEditingActivity
import javax.inject.Inject

class ExpenseEditingNavigator @Inject constructor() {

    companion object {
        const val EXPENSE_ID = "EXPENSE_ID"
        const val EXPENSE_TYPE = "EXPENSE_TYPE"
    }

    fun openEditing(context: Context, expenseId: Long) {
        val intent = Intent(context, ExpenseEditingActivity::class.java)
        intent.putExtra(EXPENSE_ID, expenseId)
        context.startActivity(intent)
    }

    fun openCreation(context: Context, type: ExpenseType? = null) {
        val intent = Intent(context, ExpenseEditingActivity::class.java)
        type?.let { intent.putExtra(EXPENSE_TYPE, it.id) }
        context.startActivity(intent)
    }
}