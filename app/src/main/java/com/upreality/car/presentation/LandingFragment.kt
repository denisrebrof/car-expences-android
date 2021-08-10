package com.upreality.car.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.R
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.presentation.editing.ExpenseEditingNavigator
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.RxLifecycleExtentions.subscribeDefault
import javax.inject.Inject
import com.upreality.car.databinding.FragmentLandingBinding as ViewBinding

@AndroidEntryPoint
class LandingFragment : Fragment(R.layout.fragment_landing) {

    @Inject
    lateinit var navigator: ExpenseEditingNavigator

    private val viewModel: LandingFragmentViewModel by activityViewModels()
    private val binding: ViewBinding by viewBinding(ViewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fineTypeButton.setOnClickListener(this@LandingFragment::onCreateExpenseClicked)
            fuelTypeButton.setOnClickListener(this@LandingFragment::onCreateExpenseClicked)
            maintenanceTypeButton.setOnClickListener(this@LandingFragment::onCreateExpenseClicked)
            logout.setOnClickListener(this@LandingFragment::onLogOutClicked)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getViewStateFlow().subscribeDefault { viewState ->
            binding.profileName.text = viewState.userName
        }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun onLogOutClicked(source: View) {
        LandingFragmentIntents.LogOut.let(viewModel::execute)
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