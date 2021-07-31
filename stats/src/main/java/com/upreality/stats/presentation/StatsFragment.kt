package com.upreality.stats.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.data.PieEntry
import com.upreality.stats.R
import com.upreality.stats.presentation.charts.ExpenseTypeChartSetup
import com.upreality.stats.presentation.charts.MainChartSetup
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import com.upreality.stats.databinding.FragmentStatsMainBinding as ViewBinding

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats_main) {

    private val viewModel: StatsFragmentViewModel by viewModels()
    private val binding: ViewBinding by viewBinding(ViewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.
    }

    override fun onStart() {
        super.onStart()
        viewModel
            .getViewState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWithLogError(this::render)
            .disposeBy(lifecycle.disposers.onStop)
    }

    private fun render(viewState: StatsViewState) {
        val pieChart = binding.statsTypesRadialDiagramCard.typesPieChart
        val pieData = viewState.typesRelationMap.map { (type, value) -> PieEntry(value, type) }
        ExpenseTypeChartSetup.setup(pieChart, ArrayList(pieData))

        val barChart = binding.statsChartCard.mainChart
        MainChartSetup.setup(barChart, arrayListOf())

        val format: (Float) -> String = { value -> String.format("%.2f", value) }
        binding.statsTypesChartCard.apply {
            viewState.ratePerLiter.let(format).let(rpl::setValue)
            viewState.ratePerMile.let(format).let(rpm::setValue)
        }
    }
}