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
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_TEXT
import com.upreality.car.expenses.data.shared.model.ExpenseType
import com.upreality.car.expenses.domain.model.DateRange
import dagger.hilt.android.AndroidEntryPoint
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import kotlinx.android.synthetic.main.activity_expences.view.*
import presentation.RxLifecycleExtentions.subscribeDefault
import java.text.SimpleDateFormat
import java.util.*
import com.upreality.car.expenses.databinding.BottonSheetExpenseFilteringBinding as ViewBinding

@AndroidEntryPoint
class ExpenseFilteringBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ExpenseFilteringViewModel by activityViewModels()
    private var binding: ViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ViewBinding.inflate(inflater, container, false).also(this::binding::set).root

    override fun onStart() {
        super.onStart()
        setListeners()

        viewModel
            .getViewState()
            .distinctUntilChanged()
            .subscribeDefault(this::applyViewState)
            .disposeBy(lifecycle.disposers.onStop)

        viewModel.getActionsFlow()
            .ofType(ExpenseFilteringAction.ShowRangePicker::class.java)
            .subscribeDefault(this::showRangePicker)
            .disposeBy(lifecycle.disposers.onStop)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun applyViewState(viewState: ExpenseFilteringViewState) = binding?.apply {

        viewState.dateRangeState.validValueOrNull()?.let { range ->
            val rangeText = "${range.startDate.getText()} - ${range.endDate.getText()}"
            dateRangeText.editText?.setText(rangeText)
        }

        when (viewState.dateRangeState.input) {
            DateRangeSelection.AllTime -> chipDateAllTime
            is DateRangeSelection.CustomRange -> chipDateCustomRange
            DateRangeSelection.Month -> chipDateMonth
            DateRangeSelection.Season -> chipDate3Month
            DateRangeSelection.Week -> chipDateWeek
            DateRangeSelection.Year -> chipDateYear
            else -> null
        }?.let(Chip::getId)?.let(dateSelectionChips::check)

        val checkedTypes = viewState.typeState.validValueOrNull() ?: setOf()
        updateTypeCheckableState(checkedTypes)
        chipTypeMaintenance.isChecked = checkedTypes.contains(ExpenseType.Maintenance)
        chipTypeFines.isChecked = checkedTypes.contains(ExpenseType.Fines)
        chipTypeFuel.isChecked = checkedTypes.contains(ExpenseType.Fuel)
    }

    private fun updateTypeCheckableState(checkedTypes: Set<ExpenseType>) = binding?.apply {
        if (checkedTypes.size == 1) {
            when (checkedTypes.first()) {
                ExpenseType.Fines -> chipTypeFines
                ExpenseType.Fuel -> chipTypeFuel
                ExpenseType.Maintenance -> chipTypeMaintenance
            }.isCheckable = false
        } else {
            chipTypeFines.isCheckable = true
            chipTypeFuel.isCheckable = true
            chipTypeMaintenance.isCheckable = true
        }
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

    private fun onChipCheckedStateChanged(chip: CompoundButton, isSelected: Boolean) =
        binding?.apply {
            when (chip) {
                chipTypeFines -> ExpenseType.Fines
                chipTypeFuel -> ExpenseType.Fuel
                chipTypeMaintenance -> ExpenseType.Maintenance
                else -> null
            }?.let { type ->
                ExpenseFilteringIntent.SetTypeFilter(type, isSelected)
            }?.let(viewModel::execute)
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onDateChipSelected(buttonView: View) = binding?.apply {
        when (buttonView) {
            chipDateAllTime -> DateRangeSelection.AllTime
            chipDateYear -> DateRangeSelection.Year
            chipDate3Month -> DateRangeSelection.Season
            chipDateMonth -> DateRangeSelection.Month
            chipDateWeek -> DateRangeSelection.Week
            else -> null
        }?.let(ExpenseFilteringIntent::ApplyDateRange)?.let(viewModel::execute)
    }

    private fun setListeners() = binding?.apply {
        chipDateAllTime.setOnClickListener(this@ExpenseFilteringBottomSheet::onDateChipSelected)
        chipDateYear.setOnClickListener(this@ExpenseFilteringBottomSheet::onDateChipSelected)
        chipDate3Month.setOnClickListener(this@ExpenseFilteringBottomSheet::onDateChipSelected)
        chipDateMonth.setOnClickListener(this@ExpenseFilteringBottomSheet::onDateChipSelected)
        chipDateWeek.setOnClickListener(this@ExpenseFilteringBottomSheet::onDateChipSelected)

        chipDateCustomRange.setOnClickListener(this@ExpenseFilteringBottomSheet::setCustomRange)
        dateRangeText.setOnClickListener(this@ExpenseFilteringBottomSheet::setCustomRange)

        chipTypeFines.setOnCheckedChangeListener(this@ExpenseFilteringBottomSheet::onChipCheckedStateChanged)
        chipTypeMaintenance.setOnCheckedChangeListener(this@ExpenseFilteringBottomSheet::onChipCheckedStateChanged)
        chipTypeFuel.setOnCheckedChangeListener(this@ExpenseFilteringBottomSheet::onChipCheckedStateChanged)
    }

    private fun Date.getText(): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(this)
    }
}