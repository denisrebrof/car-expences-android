package com.upreality.car.expenses.presentation.editing.ui

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.FineType
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.RxLifecycleExtentions.subscribeDefault
import presentation.ValidationResult
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFineBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFineFragment : Fragment(R.layout.fragment_expense_editing_fine) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        binding.chipFineSpeedLimit.setOnClickListener(this::onChipSelected)
        binding.chipFineParking.setOnClickListener(this::onChipSelected)
        binding.chipFineRoadMarking.setOnClickListener(this::onChipSelected)
        binding.chipFineOther.setOnClickListener(this::onChipSelected)
        viewModel.getViewState().subscribeDefault { viewState ->
            viewState.fineTypeState.let(this::setupFineCategory)
        }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun setupFineCategory(validationResult: ValidationResult<FinesCategories, FinesCategories>) {
        binding.chipGroup.isSelectionRequired = validationResult !is ValidationResult.Empty
        if (validationResult is ValidationResult.Empty)
            binding.chipGroup.clearCheck()
        when (validationResult.validValueOrNull()) {
            FinesCategories.SpeedLimit -> binding.chipFineSpeedLimit
            FinesCategories.Parking -> binding.chipFineParking
            FinesCategories.RoadMarking -> binding.chipFineRoadMarking
            FinesCategories.Other -> binding.chipFineOther
            else -> null
        }?.let(Chip::getId)?.let(binding.chipGroup::check)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onChipSelected(buttonView: View) {
        val fineType = when (buttonView) {
            binding.chipFineSpeedLimit -> FinesCategories.SpeedLimit
            binding.chipFineParking -> FinesCategories.Parking
            binding.chipFineRoadMarking -> FinesCategories.RoadMarking
            else -> FinesCategories.Other
        }
        viewModel.fillForm(FineType, fineType)
    }
}