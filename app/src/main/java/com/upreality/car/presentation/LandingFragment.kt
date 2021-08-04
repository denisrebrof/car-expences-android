package com.upreality.car.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.R
import com.upreality.car.databinding.FragmentLandingBinding
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.presentation.editing.ExpenseEditingNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingFragment : Fragment(R.layout.fragment_landing) {

    @Inject
    lateinit var navigator: ExpenseEditingNavigator

    private val binding: FragmentLandingBinding by viewBinding(FragmentLandingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fineTypeButton.setOnClickListener(this::onCreateExpenseClicked)
        binding.fuelTypeButton.setOnClickListener(this::onCreateExpenseClicked)
        binding.maintenanceTypeButton.setOnClickListener(this::onCreateExpenseClicked)
    }

    private fun onCreateExpenseClicked(source: View) {
        val type = when (source) {
            binding.fineTypeButton -> ExpenseType.Fines
            binding.fuelTypeButton -> ExpenseType.Fuel
            binding.maintenanceTypeButton -> ExpenseType.Maintenance
            else -> ExpenseType.Fuel
        }
        navigator.openCreation(requireContext(), type)
    }
}