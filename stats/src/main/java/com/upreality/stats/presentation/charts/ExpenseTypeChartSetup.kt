package com.upreality.stats.presentation.charts

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

object ExpenseTypeChartSetup {

    fun setup(chart: PieChart, data: ArrayList<PieEntry>, then: (PieChart) -> Unit = { }) {
        chart.apply {
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
            getData(data).let(this::setData)
            invalidate()
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

            setExtraOffsets(5f, 0f, 10f, -120f)
        }
        then(chart)
    }

    private fun getData(values: ArrayList<PieEntry>): PieData {
        val dataSet = PieDataSet(values, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.WHITE)
        return data
    }
}