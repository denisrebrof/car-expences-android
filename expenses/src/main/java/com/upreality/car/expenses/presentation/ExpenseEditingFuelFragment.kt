package com.upreality.car.expenses.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.presentation.ExpenseEditingIntent.SetInput
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.litersEt.addAfterTextChangedListener { litersText ->
            SetInput.SetLitersInput(litersText).let(viewModel::executeIntent)
        }
        binding.mileageEt.addAfterTextChangedListener { mileageText ->
            SetInput.SetMileageInput(mileageText).let(viewModel::executeIntent)
        }

        viewModel.getViewStateFlow()
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError { viewState ->
                viewState.inputState.let(this::setupInputs)
            }.disposeBy(lifecycle.disposers.onDestroy)
//        binding.mileageEt.setOnEditorActionListener(lastFieldListener)
    }

    private fun setupInputs(inputState: ExpenseEditingInputState) {
        val liters = (inputState.litersInputState as? InputState.Valid<Float>)?.input
        val mileage = (inputState.mileageInputState as? InputState.Valid<Float>)?.input
        liters?.toString()?.let(binding.litersEt::setText)
        mileage?.toString()?.let(binding.mileageEt::setText)
    }
}