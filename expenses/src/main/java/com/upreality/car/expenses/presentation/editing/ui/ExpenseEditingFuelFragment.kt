package com.upreality.car.expenses.presentation.editing.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingIntent.FillForm
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.Liters
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingKeys.Mileage
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewModel
import com.upreality.car.expenses.presentation.editing.viewmodel.ExpenseEditingViewState
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.AfterTextChangedWatcher
import presentation.RxLifecycleExtentions.subscribeDefault
import presentation.applyWithDisabledTextWatcher
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFuelBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFuelFragment : Fragment(R.layout.fragment_expense_editing_fuel) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    private val litersWatcher = AfterTextChangedWatcher { litersText ->
        FillForm(Liters, litersText, String::class).let(viewModel::execute)
    }

    private val mileageWatcher = AfterTextChangedWatcher { mileageText ->
        FillForm(Mileage, mileageText, String::class).let(viewModel::execute)
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
        binding.litersEt.applyWithDisabledTextWatcher(litersWatcher) {
            if (text.toString() != viewState.litersState.input)
                text = viewState.litersState.input ?: ""
        }
        binding.mileageEt.applyWithDisabledTextWatcher(mileageWatcher) {
            if (text.toString() != viewState.mileageState.input)
                text = viewState.mileageState.input ?: ""
        }
    }
}