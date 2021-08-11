package com.upreality.car.expenses.presentation.editing.ui

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.MaintenanceType
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.Mileage
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewModel
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewState
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.AfterTextChangedWatcher
import presentation.RxLifecycleExtentions.subscribeDefault
import presentation.ValidationResult
import presentation.applyWithDisabledTextWatcher
import com.upreality.car.expenses.databinding.FragmentExpenseEditingMaintenanceBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingMaintenanceFragment : Fragment(R.layout.fragment_expense_editing_maintenance) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    private val mileageWatcher = AfterTextChangedWatcher { mileageText ->
        viewModel.fillForm(Mileage, mileageText)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        binding.chipMaintenanceNotDefined.setOnClickListener(this::onChipSelected)
        binding.chipMaintenanceMaintenance.setOnClickListener(this::onChipSelected)
        binding.chipMaintenanceRepair.setOnClickListener(this::onChipSelected)
        binding.chipMaintenanceOther.setOnClickListener(this::onChipSelected)
        binding.mileageEt.addTextChangedListener(mileageWatcher)

        viewModel.getViewState()
            .subscribeDefault(this::applyViewState)
            .disposeBy(lifecycle.disposers.onStop)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyViewState(viewState: ExpenseEditingViewState) {
        binding.mileageEt.applyWithDisabledTextWatcher(mileageWatcher) {
            if (text.toString() != viewState.mileageState.input)
                text = viewState.mileageState.input ?: ""
        }
        val maintenanceRes = viewState.maintenanceTypeState
        binding.chipGroup.isSelectionRequired = maintenanceRes !is ValidationResult.Empty
        if (maintenanceRes is ValidationResult.Empty)
            binding.chipGroup.clearCheck()
        when (maintenanceRes.validValueOrNull()) {
            MaintenanceType.NotDefined -> binding.chipMaintenanceNotDefined
            MaintenanceType.Maintenance -> binding.chipMaintenanceMaintenance
            MaintenanceType.RepairService -> binding.chipMaintenanceRepair
            MaintenanceType.Other -> binding.chipMaintenanceOther
            else -> null
        }?.let(Chip::getId)?.let(binding.chipGroup::check)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onChipSelected(buttonView: View) {
        val maintenanceType = when (buttonView) {
            binding.chipMaintenanceNotDefined -> MaintenanceType.NotDefined
            binding.chipMaintenanceMaintenance -> MaintenanceType.Maintenance
            binding.chipMaintenanceRepair -> MaintenanceType.RepairService
            else -> MaintenanceType.Other
        }
        viewModel.fillForm(ExpenseEditingKeys.Maintenance, maintenanceType)
    }
}