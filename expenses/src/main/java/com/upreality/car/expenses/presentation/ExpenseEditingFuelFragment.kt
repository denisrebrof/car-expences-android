package com.upreality.car.expenses.presentation

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingIntent.FillForm
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.Liters
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.Mileage
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.InputState
import presentation.addAfterTextChangedListener
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFuelBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFuelFragment : Fragment(R.layout.fragment_expense_editing_fuel) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.litersEt.addAfterTextChangedListener { litersText ->
            FillForm(Liters, litersText, String::class).let(viewModel::execute)
        }
        binding.mileageEt.addAfterTextChangedListener { mileageText ->
            FillForm(Mileage, mileageText, String::class).let(viewModel::execute)
        }

        viewModel.getViewState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError { viewState ->
                trySetupInput(viewState.litersState, binding.litersEt)
                trySetupInput(viewState.mileageState, binding.mileageEt)
            }.disposeBy(lifecycle.disposers.onDestroy)
//        binding.mileageEt.setOnEditorActionListener(lastFieldListener)
    }

    private fun trySetupInput(state: InputState<String>, et: EditText) {
        (state as? InputState.Valid)?.input.let(et::setText)
        (state as? InputState.Invalid)?.reason.let(et::setError)
    }
}