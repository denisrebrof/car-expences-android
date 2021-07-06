package com.upreality.car.expenses.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import dagger.hilt.android.AndroidEntryPoint
import com.upreality.car.expenses.databinding.ActivityExpenseEditingBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingActivity : AppCompatActivity() {

    companion object {

        private const val EXPENSE_ID = "EXPENSE_ID"

        fun openInEditMode(context: Context, expenseId: Long) {
            val intent = Intent(context, ExpenseEditingActivity::class.java)
            intent.putExtra(EXPENSE_ID, expenseId)
            context.startActivity(intent)
        }
    }

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingActivityViewModel by viewModels()

    private val selectedExpenseState: SelectedExpenseState by lazy {
        intent.extras?.getLong(EXPENSE_ID)
            ?.let(SelectedExpenseState::Defined)
            ?: SelectedExpenseState.NotDefined
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_editing)
        CostTextChangedListener().let(binding.costEt::addTextChangedListener)

    }

    inner class CostTextChangedListener : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {
            TODO("Not yet implemented")
        }

    }

    private sealed class SelectedExpenseState {
        object NotDefined : SelectedExpenseState()
        data class Defined(val id: Long) : SelectedExpenseState()
    }
}