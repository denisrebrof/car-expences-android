package com.upreality.car.expenses.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButton
import com.upreality.car.expenses.R
import com.upreality.car.expenses.data.shared.model.ExpenseType
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
    private lateinit var navController: NavController

    private val defaultFieldError: String = "Invalid input 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_editing)
        binding.costEt.addAfterTextChangedListener { costText ->
            ExpenseEditingIntent.SetInput.SetCostInput(costText).let(viewModel::executeIntent)
        }
        binding.expenseTypeSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked)
                return@addOnButtonCheckedListener
            when (checkedId) {
                binding.fineTypeButton.id -> ExpenseType.Fines
                binding.fuelTypeButton.id -> ExpenseType.Fuel
                else -> return@addOnButtonCheckedListener
            }.let(ExpenseEditingIntent.SetInput::SetTypeInput).let(viewModel::executeIntent)
        }

        val navHostId = binding.expenseDetailsContainer.id
        val navHostFragment = supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
        navController = navHostFragment.navController

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
        val viewStateFlow = viewModel.getViewStateFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        viewStateFlow
            .subscribeWithLogError { applyInputState(it.inputState.costInputState, it.isValid) }
            .disposeBy(lifecycle.disposers.onStop)

        viewStateFlow
            .map(ExpenseEditingViewState::inputState)
            .map(ExpenseEditingInputState::typeInputState)
            .ofType(InputState.Valid::class.java)
            .map(InputState.Valid<*>::input)
            .ofType(ExpenseType::class.java)
            .distinctUntilChanged(ExpenseType::id)
            .subscribeWithLogError(this::applySelectedType)
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun applyInputState(costInputState: InputState<Float>, isValid: Boolean) {
        val setErrorIfDefined: (InputState<Float>, EditText) -> Unit = { inputState, editText ->
            val reason = (inputState as? InputState.Invalid)?.let { it.reason ?: defaultFieldError }
            editText.error = reason
        }
        setErrorIfDefined(costInputState, binding.costEt)
        binding.applyButton.isEnabled = isValid
    }

    override fun onBackPressed() = finish()

    private fun setupViewState(viewState: ExpenseEditingViewState) {
        val cost = (viewState.inputState.mileageInputState as? InputState.Valid<Float>)?.input
        cost?.toString()?.let(binding.costEt::setText)
        val type = (viewState.inputState.typeInputState as? InputState.Valid<ExpenseType>)?.input
        type?.let(this::applySelectedType)
    }

    private fun applySelectedType(type: ExpenseType) {
        val (selectedButton, action) = when (type) {
            ExpenseType.Fines -> binding.fineTypeButton to R.id.action_global_expenseEditingFineFragment
            ExpenseType.Fuel -> binding.fuelTypeButton to R.id.action_global_expenseEditingFuelFragment
            else -> null to null
        }
        (selectedButton as? MaterialButton)?.isChecked = true
        action?.let(navController::navigate)
    }

//    private val lastFieldListener = TextView.OnEditorActionListener { view, actionId, event ->
//        if (actionId == EditorInfo.IME_ACTION_DONE)
//            onCreateOrUpdateClicked(view)
//
//        actionId == EditorInfo.IME_ACTION_NEXT
//    }
}