package com.upreality.car.expenses.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
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
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel as ViewModel

@AndroidEntryPoint
class ExpenseEditingActivity : AppCompatActivity() {

    companion object {

        const val EXPENSE_ID = "EXPENSE_ID"

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

    private val defaultFieldError: String = "Invalid input 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_editing)
        binding.costEt.addAfterTextChangedListener(viewModel::setCostInput)
        (viewModel.selectedState as? SelectedExpenseState.Defined)
            ?.let(SelectedExpenseState.Defined::id)
            ?.let(this::setupSelectedExpense)

        val navHostId = binding.expenseDetailsContainer.id
        val navHostFragment = supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
        val navController = navHostFragment.navController
        binding.button1.setOnClickListener { navController.navigate(R.id.action_global_expenseEditingFuelFragment) }
        binding.button2.setOnClickListener { navController.navigate(R.id.action_global_expenseEditingFineFragment) }

        binding.applyButton.text = when (viewModel.selectedState) {
            is SelectedExpenseState.Defined -> "Update Expense"
            is SelectedExpenseState.NotDefined -> "Create Expense"
        }
        binding.deleteButton.isVisible = viewModel.selectedState is SelectedExpenseState.Defined
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
        binding.applyButton.isEnabled = viewState.isValid
    }

    private fun onDeleteClicked(v: View) {
        viewModel.deleteExpense()
            .subscribeWithLogError { finish() }
            .disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun onCreateOrUpdateClicked(v: View) {
        val actionMaybe = when (viewModel.selectedState) {
            is SelectedExpenseState.Defined -> viewModel.updateExpense()
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
    }

//    private val lastFieldListener = TextView.OnEditorActionListener { view, actionId, event ->
//        if (actionId == EditorInfo.IME_ACTION_DONE)
//            onCreateOrUpdateClicked(view)
//
//        actionId == EditorInfo.IME_ACTION_NEXT
//    }
}