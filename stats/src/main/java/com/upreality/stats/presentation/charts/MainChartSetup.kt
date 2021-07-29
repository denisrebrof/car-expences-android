package com.upreality.stats.presentation.charts

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


object MainChartSetup {

    fun setup(chart: BarChart, data: ArrayList<BarEntry>, then: (BarChart) -> Unit = { }) {
        chart.apply {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            setMaxVisibleValueCount(60)
            // scaling can now only be done on x- and y-axis separately
            setPinchZoom(false)

            setDrawGridBackground(false)
            // setDrawYLabels(false);

            val xAxis = xAxis
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // only intervals of 1 day

            xAxis.labelCount = 7
            xAxis.setValueFormatter(DefaultValueFormatter(10))

            val leftAxis = axisLeft
            leftAxis.setLabelCount(8, false)
            leftAxis.setValueFormatter(DefaultValueFormatter(10))
            leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            val rightAxis = axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setLabelCount(8, false)
            rightAxis.setValueFormatter(DefaultValueFormatter(10))
            rightAxis.spaceTop = 15f
            rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            val l = legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)
            l.form = LegendForm.SQUARE
            l.formSize = 9f
            l.textSize = 11f
            l.xEntrySpace = 4f

            chart.data = getData(data)
            chart.invalidate()
        }
        then(chart)
    }

    private fun getData(values: ArrayList<BarEntry>): BarData {
        val values: ArrayList<BarEntry> = ArrayList()
        val start = 1

        for (i in start until start + 5) {
            val `val` = (Math.random() * (2 + 1)) as Float
            values.add(BarEntry(i.toFloat(), `val`))
        }

        val set1 = BarDataSet(values, "The year 2017")
        set1.setDrawIcons(false)
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.barWidth = 0.9f
        return data
    }
}