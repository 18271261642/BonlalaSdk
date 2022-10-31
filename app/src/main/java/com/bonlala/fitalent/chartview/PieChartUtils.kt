package com.bonlala.fitalent.chartview

import android.content.Context
import android.graphics.Color
import com.bonlala.fitalent.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

/**
 * Created by Admin
 *Date 2022/10/20
 */
class PieChartUtils {


    private var chartView : PieChart ?=null

    fun setPieChart(pieChart : PieChart){
        chartView = pieChart
        pieChart.setUsePercentValues(true)
        pieChart.description?.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.95f

//        pieChart?.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"))
//        pieChart?.setCenterText(generateCenterSpannableText())

        pieChart.setExtraOffsets(20f, 0f, 20f, 0f)

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        pieChart.setDrawCenterText(true)

        pieChart.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        // chart.spin(2000, 0, 360);
        val l: Legend? = pieChart.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l?.orientation = Legend.LegendOrientation.VERTICAL
        l?.setDrawInside(false)
        l?.isEnabled = false
    }


    private var parties  = mutableListOf<String>(" "," "," "," ")

    private val colorArray = mutableListOf<Int>(
        R.color.hr_color_1,
        R.color.hr_color_2,
        R.color.hr_color_3,
        R.color.hr_color_4,
        R.color.hr_color_5,
        R.color.hr_color_6
        )

    private val emptyColor = Color.parseColor("#E8E9ED")

     fun setData(pointList : List<Int>, range: Float,context : Context,isEmpty : Boolean) {
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

         if(isEmpty){
             entries.add(PieEntry(100f,""))
             colors.add(emptyColor)
         }else{
             pointList.forEachIndexed { index, i ->
                 if(i != 0){
                     entries.add(PieEntry(i.toFloat(),parties.get(index)))
                     colors.add(context.resources.getColor(colorArray[index]))
                 }

             }
         }

        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // add a lot of colors

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        //   data.setValueTypeface(tf)
        chartView?.setData(data)

        // undo all highlights
        chartView?.highlightValues(null)
        chartView?.invalidate()
    }

}