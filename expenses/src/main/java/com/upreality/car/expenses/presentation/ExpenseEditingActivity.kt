package com.upreality.car.expenses.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
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
        binding.mileageEt.setOnEditorActionListener(lastFieldListener)
        (selectedExpenseState as? SelectedExpenseState.Defined)
            ?.let(SelectedExpenseState.Defined::id)
            ?.let(this::setupSelectedExpense)


        binding.applyButton.text = when (selectedExpenseState) {
            is SelectedExpenseState.Defined -> "Update Expense"
            is SelectedExpenseState.NotDefined -> "Create Expense"
        }
        binding.deleteButton.isVisible = selectedExpenseState is SelectedExpenseState.Defined
        binding.applyButton.setOnClickListener(this::onCreateOrUpdateClicked)
        binding.deleteButton.setOnClickListener(this::onDeleteClicked)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getViewStateFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError(this::applyViewState)
            .disposeBy(lifecycle.disposers.onStop)
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

    private fun onDeleteClicked(v: View) {
        val definedState = (selectedExpenseState as? SelectedExpenseState.Defined) ?: return
        definedState
            .let(SelectedExpenseState.Defined::id)
            .let(viewModel::deleteExpense)
            .subscribeWithLogError { finish() }
            .disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun onCreateOrUpdateClicked(v: View) {
        val actionMaybe = when (val selectedState = selectedExpenseState) {
            is SelectedExpenseState.Defined -> viewModel.updateExpense(selectedState.id)
            is SelectedExpenseState.NotDefined -> viewModel.createExpense()
        }
        actionMaybe
            .subscribeOn(Schedulers.io())
            .subscribeWithLogError {
                finish()
            }.disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun setupSelectedExpense(expenseId: Long) {
        viewModel.getExpense(expenseId)
            .subscribeOn(Schedulers.io())
            .subscribeWithLogError(this::setupExpense)
            .disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun setupExpense(expense: Expense) {
        expense.cost.toString().let(binding.costEt::setText)
        (expense as? Expense.Fuel)?.let { fuelExpense ->
            fuelExpense.liters.toString().let(binding.litersEt::setText)
            fuelExpense.mileage.toString().let(binding.mileageEt::setText)
        }
    }

    private val lastFieldListener = TextView.OnEditorActionListener { view, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE)
            onCreateOrUpdateClicked(view)

        actionId == EditorInfo.IME_ACTION_NEXT
    }

    private sealed class SelectedExpenseState {
        object NotDefined : SelectedExpenseState()
        data class Defined(val id: Long) : SelectedExpenseState()
    }
}