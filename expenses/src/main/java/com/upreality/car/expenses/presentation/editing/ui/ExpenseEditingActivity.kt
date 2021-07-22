package com.upreality.car.expenses.presentation.editing.ui

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.presentation.editing.viewmodel.*
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingIntent.FillForm
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewModel.*
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.AfterTextChangedWatcher
import presentation.InputState
import presentation.RxLifecycleExtentions.subscribeDefault
import presentation.applyWithDisabledTextWatcher
import java.util.*
import com.upreality.car.expenses.databinding.ActivityExpenseEditingBinding as ViewBinding
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingDateInputValue as DateInputValue
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewModel as ViewModel

@AndroidEntryPoint
class ExpenseEditingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ViewModel by viewModels()
    private lateinit var navController: NavController

    private val defaultFieldError: String = "Invalid input"

    private val costWatcher = AfterTextChangedWatcher { costText ->
        FillForm(
            ExpenseEditingKeys.Cost,
            costText,
            String::class
        ).let(viewModel::execute)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_editing)

        binding.editingToolbar.let(this::setSupportActionBar)
        binding.editingToolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.costEt.addTextChangedListener(costWatcher)

        binding.expenseTypeSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked)
                return@addOnButtonCheckedListener
            val type = when (checkedId) {
                binding.fineTypeButton.id -> ExpenseType.Fines
                binding.fuelTypeButton.id -> ExpenseType.Fuel
                else -> return@addOnButtonCheckedListener
            }
            FillForm(ExpenseEditingKeys.Type, type, ExpenseType::class).let(viewModel::execute)
        }

        binding.dateInputLayout.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked)
                return@addOnButtonCheckedListener
            when (checkedId) {
                binding.dateSelectorToday.id -> ExpenseEditingDateSelectionType.Today
                binding.dateSelectorYesterday.id -> ExpenseEditingDateSelectionType.Yesterday
                else -> ExpenseEditingDateSelectionType.Custom
            }.let(ExpenseEditingIntent::SelectDate).let(viewModel::execute)
        }

        val navHostId = binding.expenseDetailsContainer.id
        val navHostFragment = supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
        navController = navHostFragment.navController
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        val viewStateFlow = viewModel.getViewState()

        viewStateFlow
            .subscribeDefault(this::applyViewState)
            .disposeBy(lifecycle.disposers.onStop)

        viewStateFlow
            .map(ExpenseEditingViewState::typeState)
            .ofType(InputState.Valid::class.java)
            .map(InputState.Valid<*>::input)
            .ofType(ExpenseType::class.java)
            .distinctUntilChanged(ExpenseType::id)
            .subscribeDefault(this::applySelectedType)
            .disposeBy(lifecycle.disposers.onStop)

        viewStateFlow
            .map(ExpenseEditingViewState::dateState)
            .ofType(InputState.Valid::class.java)
            .map(InputState.Valid<*>::input)
            .ofType(DateInputValue::class.java)
            .distinctUntilChanged()
            .subscribeDefault(this::applySelectedDateState)
            .disposeBy(lifecycle.disposers.onStop)

        viewModel.getActionState().subscribeDefault { action ->
            when (action) {
                ExpenseEditingAction.Finish -> finish()
                is ExpenseEditingAction.ShowDatePicker -> TODO()
            }
        }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun applyViewState(viewState: ExpenseEditingViewState) {
        val setErrorIfDefined: (InputState<String>, EditText) -> Unit = { inputState, editText ->
            val reason = (inputState as? InputState.Invalid)?.let { it.reason ?: defaultFieldError }
            reason?.let(editText::setError)
        }

        setErrorIfDefined(viewState.costState, binding.costEt)

        binding.costEt.applyWithDisabledTextWatcher(costWatcher) {
            if (text.toString() != viewState.costState.input)
                text = viewState.costState.input ?: ""
        }

        binding.applyButton.isEnabled = viewState.isValid
        binding.deleteButton.isVisible = !viewState.newExpenseCreation
        binding.applyButton.text = when (viewState.newExpenseCreation) {
            true -> "Create expense"
            else -> "Update expense"
        }
        binding.applyButton.setOnClickListener {
            ExpenseEditingIntent.Submit.let(viewModel::execute)
        }
        binding.deleteButton.setOnClickListener {
            ExpenseEditingIntent.Delete.let(viewModel::execute)
        }

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            title = if (viewState.newExpenseCreation) "New Expense" else "Update Expense"
        }
    }

    private fun applySelectedType(type: ExpenseType) {
        val (selectedButton, action) = when (type) {
            ExpenseType.Fines -> binding.fineTypeButton to R.id.action_global_expenseEditingFineFragment
            ExpenseType.Fuel -> binding.fuelTypeButton to R.id.action_global_expenseEditingFuelFragment
            else -> null to null
        }
        selectedButton?.isChecked = true
        action?.let(navController::navigate)
    }

    private fun applySelectedDateState(state: DateInputValue) {
        when (state) {
            DateInputValue.Today -> binding.dateSelectorToday
            DateInputValue.Yesterday -> binding.dateSelectorYesterday
            else -> binding.dateSelectorSelect
        }.isChecked = true
    }

    override fun onBackPressed() = ExpenseEditingIntent.Close.let(viewModel::execute)

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Calendar.getInstance()
            .apply { set(year, month, dayOfMonth) }
            .let(Calendar::getTime)
            .let(DateInputValue::Custom)
            .let { state ->
                FillForm(ExpenseEditingKeys.SpendDate, state, DateInputValue::class)
            }.let(viewModel::execute)

    }
}