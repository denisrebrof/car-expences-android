package com.upreality.stats.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.upreality.stats.R
import dagger.hilt.android.AndroidEntryPoint
import com.upreality.stats.databinding.FragmentStatsMainBinding as ViewBinding


@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats_main) {

    private val viewModel: StatsFragmentViewModel by viewModels()
    private val binding: ViewBinding by viewBinding(ViewBinding::bind)

    protected val types = arrayOf(
        "Fuel", "Fines", "Maintenance", "Party D", "Party E", "Party F", "Party G", "Party H",
        "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
        "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
        "Party Y", "Party Z"
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.statsTypesRadialDiagramCard.typesPieChart.apply {
            setBackgroundColor(Color.WHITE)
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 24f
            transparentCircleRadius = 28f
            setDrawCenterText(true)
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            maxAngle = 180f // HALF CHART
            rotationAngle = 180f
//            setCenterTextOffset(0, -20)
            setData(3, 100f)
            animateY(1400, Easing.EaseInOutQuad)
            val l: Legend = legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 0f

            // entry label styling
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)

            setExtraOffsets(5f,0f,10f,-120f)
        }

    }

    private fun setData(count: Int, range: Float) {
        val values: ArrayList<PieEntry> = ArrayList()
        for (i in 0 until count) {
            values.add(
                PieEntry(
                    (Math.random() * range + range / 5).toFloat(),
                    types[i % types.size]
                )
            )
        }
        val dataSet = PieDataSet(values, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.WHITE)
        binding.statsTypesRadialDiagramCard.typesPieChart.data = data
        binding.statsTypesRadialDiagramCard.typesPieChart.invalidate()
    }

}