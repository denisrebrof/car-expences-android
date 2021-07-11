package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.addAfterTextChangedListener
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFuelBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFuelFragment : Fragment(R.layout.fragment_expense_editing_fuel) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.litersEt.addAfterTextChangedListener(viewModel::setLitersInput)
        binding.mileageEt.addAfterTextChangedListener(viewModel::setMileageInput)

        (viewModel.selectedState as? SelectedExpenseState.Defined)
            ?.let(SelectedExpenseState.Defined::id)
            ?.let(this::setupSelectedExpense)
//        binding.mileageEt.setOnEditorActionListener(lastFieldListener)
    }

    private fun setupSelectedExpense(expenseId: Long) {
        viewModel.getExpense(expenseId)
            .subscribeOn(Schedulers.io())
            .subscribeWithLogError(this::setupExpense)
            .disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun setupExpense(expense: Expense) {
        (expense as? Expense.Fuel)?.let { fuelExpense ->
            fuelExpense.liters.toString().let(binding.litersEt::setText)
            fuelExpense.mileage.toString().let(binding.mileageEt::setText)
        }
    }
}