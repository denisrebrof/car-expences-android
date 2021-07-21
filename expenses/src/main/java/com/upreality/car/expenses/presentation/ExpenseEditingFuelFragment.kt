package com.upreality.car.expenses.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingIntent.FillForm
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.Liters
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.Mileage
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingViewState
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.RxLifecycleExtentions.subscribeDefault
import presentation.getAfterTextChangedWatcher
import presentation.silentApplyText
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFuelBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFuelFragment : Fragment(R.layout.fragment_expense_editing_fuel) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    private val litersWatcher by lazy {
        binding.litersEt.getAfterTextChangedWatcher { litersText ->
            FillForm(Liters, litersText, String::class).let(viewModel::execute)
        }
    }

    private val mileageWatcher by lazy {
        binding.mileageEt.getAfterTextChangedWatcher { mileageText ->
            FillForm(Mileage, mileageText, String::class).let(viewModel::execute)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        binding.litersEt.addTextChangedListener(litersWatcher)
        binding.mileageEt.addTextChangedListener(mileageWatcher)

        viewModel.getViewState()
            .subscribeDefault(this::applyViewState)
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun applyViewState(viewState: ExpenseEditingViewState) {
        binding.litersEt.silentApplyText(viewState.litersState.input ?: "", litersWatcher)
        binding.mileageEt.silentApplyText(viewState.mileageState.input ?: "", mileageWatcher)
    }
}