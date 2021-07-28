package com.upreality.stats.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.data.PieEntry
import com.upreality.stats.R
import dagger.hilt.android.AndroidEntryPoint
import domain.subscribeWithLogError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sellmair.disposer.disposeBy
import io.sellmair.disposer.disposers
import presentation.RxLifecycleExtentions.subscribeDefault
import com.upreality.stats.databinding.FragmentStatsMainBinding as ViewBinding


@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats_main) {

    private val viewModel: StatsFragmentViewModel by viewModels()
    private val binding: ViewBinding by viewBinding(ViewBinding::bind)

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

        binding.statsTypesChartCard.apply {
            viewState.ratePerLiter.toString().let(rpl::setValue)
            viewState.ratePerMile.toString().let(rpm::setValue)
        }
    }
}