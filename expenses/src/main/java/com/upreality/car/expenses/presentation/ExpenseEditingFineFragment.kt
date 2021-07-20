package com.upreality.car.expenses.presentation

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.upreality.car.expenses.R
import com.upreality.car.expenses.domain.model.FinesCategories
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingIntent.FillForm
import com.upreality.car.expenses.presentation.ExpenseEditingViewModel.ExpenseEditingKeys.FineType
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.InputState
import com.upreality.car.expenses.databinding.FragmentExpenseEditingFineBinding as ViewBinding

@AndroidEntryPoint
class ExpenseEditingFineFragment : Fragment(R.layout.fragment_expense_editing_fine) {

    private val binding: ViewBinding by viewBinding(ViewBinding::bind)
    private val viewModel: ExpenseEditingViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chipFineSpeedLimit.setOnCheckedChangeListener(this::onChipSelected)
        binding.chipFineParking.setOnCheckedChangeListener(this::onChipSelected)
        binding.chipFineRoadMarking.setOnCheckedChangeListener(this::onChipSelected)
        binding.chipFineOther.setOnCheckedChangeListener(this::onChipSelected)
        viewModel.getViewState()
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError { viewState ->
                (viewState.fineTypeState as? InputState.Valid)
                    ?.let(InputState.Valid<FinesCategories>::input)
                    ?.let(this::setupFineCategory)
            }.disposeBy(lifecycle.disposers.onDestroy)
    }

    private fun setupFineCategory(type: FinesCategories) {
        when (type) {
            FinesCategories.SpeedLimit -> binding.chipFineSpeedLimit
            FinesCategories.Parking -> binding.chipFineParking
            FinesCategories.RoadMarking -> binding.chipFineRoadMarking
            FinesCategories.Other -> binding.chipFineOther
        }.isSelected = true
    }

    private fun onChipSelected(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isChecked)
            return
        val fineType = when (buttonView) {
            binding.chipFineSpeedLimit -> FinesCategories.SpeedLimit
            binding.chipFineParking -> FinesCategories.Parking
            binding.chipFineRoadMarking -> FinesCategories.RoadMarking
            else -> FinesCategories.Other
        }
        FillForm(FineType, fineType, FinesCategories::class).let(viewModel::execute)
    }
}