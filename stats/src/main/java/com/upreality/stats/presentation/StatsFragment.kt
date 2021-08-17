package com.upreality.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.data.PieEntry
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringAction
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringBottomSheet
import com.upreality.car.expenses.presentation.fitering.ExpenseFilteringViewModel
import com.upreality.stats.R
import com.upreality.stats.presentation.charts.ExpenseTypeChartSetup
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import com.upreality.stats.databinding.FragmentStatsMainBinding as ViewBinding

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats_main) {

    private val filteringViewModel: ExpenseFilteringViewModel by activityViewModels()
    private val viewModel: StatsFragmentViewModel by viewModels()
    private val binding: ViewBinding by viewBinding(ViewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.filterButton.setOnClickListener {
            ExpenseFilteringBottomSheet().show(parentFragmentManager, "")
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel
            .getViewState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError(this::render)
            .disposeBy(lifecycle.disposers.onStop)

        filteringViewModel
            .getActionsFlow()
            .observeOn(AndroidSchedulers.mainThread())
            .ofType(ExpenseFilteringAction.ApplyFilters::class.java)
            .map(ExpenseFilteringAction.ApplyFilters::filters)
            .map(StatsIntents::SetFilters)
            .subscribeWithLogError(viewModel::execute)
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun render(viewState: StatsViewState) {
        val pieChart = binding.statsTypesRadialDiagramCard.typesPieChart
        val pieData = viewState.typesRelationMap.map { (type, value) -> PieEntry(value, type) }
        ExpenseTypeChartSetup.setup(pieChart, ArrayList(pieData))

//        val barChart = binding.statsChartCard.mainChart
//        MainChartSetup.setup(barChart, arrayListOf())

        val format: (Float) -> String = { value -> String.format("%.2f", value) }
        binding.statsTypesChartCard.apply {
            viewState.ratePerLiter.let(format).let(rpl::setValue)
            viewState.ratePerMile.let(format).let(rpm::setValue)
            viewState.rate.let(format).let(rate::setValue)
        }
    }
}