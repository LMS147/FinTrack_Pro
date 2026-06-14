package com.example.fintrackpro.ui.reports

import com.example.fintrackpro.R
import com.example.fintrackpro.data.entity.CategorySpendingSummary
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

object PieChartManager {

    fun setupPieChart(pieChart: PieChart, data: List<CategorySpendingSummary>) {
        if (data.isEmpty()) {
            pieChart.clear()
            pieChart.centerText = "No data"
            return
        }

        val entries = data.map { PieEntry(it.total.toFloat(), it.name) }
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            R.color.primary_blue,
            R.color.primary_green,
            R.color.error_red,
            R.color.purple_200,
            R.color.teal_200,
            R.color.orange
        ).map { pieChart.context.getColor(it) }

        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = android.graphics.Color.WHITE

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 40f
        pieChart.setHoleColor(android.graphics.Color.TRANSPARENT)
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}