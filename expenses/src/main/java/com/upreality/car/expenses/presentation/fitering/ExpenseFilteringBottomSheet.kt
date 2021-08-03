package com.upreality.car.expenses.presentation.fitering

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.RxLifecycleExtentions.subscribeDefault
import java.util.*
import com.upreality.car.expenses.databinding.BottonSheetExpenseFilteringBinding as ViewBinding

@AndroidEntryPoint
class ExpenseFilteringBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ExpenseFilteringViewModel by activityViewModels()

    private var binding: ViewBinding? = null
    private val requireBinding: ViewBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ViewBinding.inflate(inflater, container, false).also(this::binding::set).root

    override fun onStart() {
        super.onStart()
        setListeners()
        viewModel.getViewState().subscribeDefault { viewState ->

        }.disposeBy(lifecycle.disposers.onStop)

        viewModel.getActionsFlow()
            .ofType(ExpenseFilteringAction.ShowRangePicker::class.java)
            .subscribeDefault(this::showRangePicker)
            .disposeBy(lifecycle.disposers.onStop)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun showRangePicker(action: ExpenseFilteringAction.ShowRangePicker) {
        val picker = MaterialDatePicker.Builder.dateRangePicker().apply {
            Pair(action.fromTime, action.toTime).let(this::setSelection)
        }.build()
        picker.addOnPositiveButtonClickListener(this::onDateRangeSelected)
        picker.show(parentFragmentManager, tag)
    }

    private fun onDateRangeSelected(range: Pair<Long, Long>) {
        val fromTime = Date(range.first ?: return)
        val toTime = Date(range.second ?: return)
        DateRange(fromTime, toTime)
            .let(DateRangeSelection::CustomRange)
            .let(ExpenseFilteringIntent::ApplyDateRange)
            .let(viewModel::execute)
    }

    private fun setCustomRange(source: View) {
        ExpenseFilteringIntent.ShowDateRange.let(viewModel::execute)
    }

    private fun onChipCheckedStateChanged(chip: CompoundButton, isSelected: Boolean) {
        when (chip) {
            requireBinding.chipTypeFines -> ExpenseType.Fines
            requireBinding.chipTypeFuel -> ExpenseType.Fuel
            requireBinding.chipTypeMaintenance -> ExpenseType.Maintenance
            else -> null
        }?.let { type ->
            ExpenseFilteringIntent.SetTypeFilter(type, isSelected)
        }?.let(viewModel::execute)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onDateChipSelected(buttonView: View) {
        when (buttonView) {
            requireBinding.chipDateAllTime -> DateRangeSelection.AllTime
            requireBinding.chipDateYear -> DateRangeSelection.Year
            requireBinding.chipDate3Month -> DateRangeSelection.Season
            requireBinding.chipDateMonth -> DateRangeSelection.Month
            requireBinding.chipDateWeek -> DateRangeSelection.Week
            else -> null
        }?.let(ExpenseFilteringIntent::ApplyDateRange)?.let(viewModel::execute)
    }

    private fun setListeners() {
        requireBinding.chipDateAllTime.setOnClickListener(this::onDateChipSelected)
        requireBinding.chipDateYear.setOnClickListener(this::onDateChipSelected)
        requireBinding.chipDate3Month.setOnClickListener(this::onDateChipSelected)
        requireBinding.chipDateMonth.setOnClickListener(this::onDateChipSelected)
        requireBinding.chipDateWeek.setOnClickListener(this::onDateChipSelected)

        requireBinding.chipDateCustomRange.setOnClickListener(this::setCustomRange)
        requireBinding.dateRangeText.setOnClickListener(this::setCustomRange)

        requireBinding.chipTypeFines.setOnCheckedChangeListener(this::onChipCheckedStateChanged)
        requireBinding.chipTypeMaintenance.setOnCheckedChangeListener(this::onChipCheckedStateChanged)
        requireBinding.chipTypeFuel.setOnCheckedChangeListener(this::onChipCheckedStateChanged)
    }
}