package com.upreality.car.expenses.presentation

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
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
    override fun onStart() {
        super.onStart()
        binding.chipFineSpeedLimit.setOnClickListener(this::onChipSelected)
        binding.chipFineParking.setOnClickListener(this::onChipSelected)
        binding.chipFineRoadMarking.setOnClickListener(this::onChipSelected)
        binding.chipFineOther.setOnClickListener(this::onChipSelected)
        viewModel.getViewState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError { viewState ->
                (viewState.fineTypeState as? InputState.Valid)
                    ?.let(InputState.Valid<FinesCategories>::input)
                    ?.let(this::setupFineCategory)
            }.disposeBy(lifecycle.disposers.onStop)
    }

    private fun setupFineCategory(type: FinesCategories) {
        when (type) {
            FinesCategories.SpeedLimit -> binding.chipFineSpeedLimit
            FinesCategories.Parking -> binding.chipFineParking
            FinesCategories.RoadMarking -> binding.chipFineRoadMarking
            FinesCategories.Other -> binding.chipFineOther
        }.let(Chip::getId).let(binding.chipGroup::check)
    }

    private fun onChipSelected(buttonView: View) {
        val fineType = when (buttonView) {
            binding.chipFineSpeedLimit -> FinesCategories.SpeedLimit
            binding.chipFineParking -> FinesCategories.Parking
            binding.chipFineRoadMarking -> FinesCategories.RoadMarking
            else -> FinesCategories.Other
        }
        FillForm(FineType, fineType, FinesCategories::class).let(viewModel::execute)
    }
}