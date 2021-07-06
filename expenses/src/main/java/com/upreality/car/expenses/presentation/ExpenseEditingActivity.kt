package com.upreality.car.expenses.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.InputState
import presentation.addAfterTextChangedListener
import com.upreality.car.expenses.databinding.ActivityExpenseEditingBinding as ViewBinding
import com.upreality.car.expenses.presentation.ExpenseEditingActivityViewModel as ViewModel

@AndroidEntryPoint
class ExpenseEditingActivity : AppCompatActivity() {

    companion object {

        private const val EXPENSE_ID = "EXPENSE_ID"

        fun openInEditMode(context: Context, expenseId: Long) {
            val intent = Intent(context, ExpenseEditingActivity::class.java)
            intent.putExtra(EXPENSE_ID, expenseId)
            context.startActivity(intent)
        }

        fun openInCreateMode(context: Context) {
            val intent = Intent(context, ExpenseEditingActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ViewModel by viewModels()

    private val selectedExpenseState: SelectedExpenseState by lazy {
        intent.extras?.getLong(EXPENSE_ID)
            ?.let(SelectedExpenseState::Defined)
            ?: SelectedExpenseState.NotDefined
    }

    private val defaultFieldError: String by lazy {
        "Invalid input 2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_editing)
        binding.costEt.addAfterTextChangedListener(viewModel::setCostInput)
        binding.litersEt.addAfterTextChangedListener(viewModel::setLitersInput)
        binding.mileageEt.addAfterTextChangedListener(viewModel::setMileageInput)
        (selectedExpenseState as? SelectedExpenseState.Defined)
            ?.let(SelectedExpenseState.Defined::id)
            ?.let(this::setupSelectedExpense)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getViewStateFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::applyViewState) {
                Log.e(this.localClassName, "Error while get view state: $it")
            }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun applyViewState(viewState: ExpenseEditingViewState) {
        val setErrorIfDefined: (InputState<Float>, EditText) -> Unit = { inputState, editText ->
            val reason = (inputState as? InputState.Invalid)?.let { it.reason ?: defaultFieldError }
            editText.error = reason
        }
        setErrorIfDefined(viewState.inputState.costInputState, binding.costEt)
        setErrorIfDefined(viewState.inputState.litersInputState, binding.litersEt)
        setErrorIfDefined(viewState.inputState.mileageInputState, binding.mileageEt)
        binding.applyButton.isEnabled = viewState.isValid
    }

    private fun setupSelectedExpense(expenseId: Long) {
        viewModel.getExpense(expenseId)
            .subscribeOn(Schedulers.io())
            .subscribe(this::setupExpense) {
                Log.e(this.localClassName, "Error while setup expense: $it")
            }.disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun setupExpense(expense: Expense) {
        expense.cost.toString().let(binding.costEt::setText)
        (expense as? Expense.Fuel)?.let { fuelExpense ->
            fuelExpense.liters.toString().let(binding.litersEt::setText)
            fuelExpense.mileage.toString().let(binding.mileageEt::setText)
        }
    }

    private sealed class SelectedExpenseState {
        object NotDefined : SelectedExpenseState()
        data class Defined(val id: Long) : SelectedExpenseState()
    }
}