package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.domain.model.expence.Expense
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFineBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFineFragment : Fragment(R.layout.fragment_expense_editing_fine) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chipFineSpeedLimit.setOnCheckedChangeListener(this::onChipSelected)
        binding.chipFineParking.setOnCheckedChangeListener(this::onChipSelected)
        binding.chipFineRoadMarking.setOnCheckedChangeListener(this::onChipSelected)
        binding.chipFineOther.setOnCheckedChangeListener(this::onChipSelected)

        (viewModel.selectedState as? SelectedExpenseState.Defined)
            ?.let(SelectedExpenseState.Defined::id)
            ?.let(this::setupSelectedExpense)
    }

    private fun setupSelectedExpense(expenseId: Long) {
        viewModel.getExpense(expenseId)
            .subscribeOn(Schedulers.io())
            .subscribeWithLogError(this::setupExpense)
            .disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun setupExpense(expense: Expense) {
        (expense as? Expense.Fine)?.let { fineExpense ->
            when (fineExpense.type) {
                FinesCategories.SpeedLimit -> binding.chipFineSpeedLimit
                FinesCategories.Parking -> binding.chipFineParking
                FinesCategories.RoadMarking -> binding.chipFineRoadMarking
                FinesCategories.Other -> binding.chipFineOther
            }.isSelected = true
        }
    }

    private fun onChipSelected(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isChecked)
            return
        when (buttonView) {
            binding.chipFineSpeedLimit -> FinesCategories.SpeedLimit
            binding.chipFineParking -> FinesCategories.Parking
            binding.chipFineRoadMarking -> FinesCategories.RoadMarking
            else -> FinesCategories.Other
        }.let(ExpenseEditingIntent.SetInput::SetFineTypeInput).let(viewModel::executeIntent)
    }
}