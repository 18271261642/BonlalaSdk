package com.bonlala.fitalent.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.model.SleepModel
import com.bonlala.fitalent.view.CusSleepDescView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_sleep_desc_layout.*
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/9
 */
class HistorySleepDescDialog : AppCompatDialog {


    //饼状图
    private var pieChart : PieChart ?= null

    //进度条
    private var scheduleView : CusSleepDescView?= null

    //睡眠的对象
    private var sleepModel : SleepModel ?= null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context,theme: Int) : super(context,theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_sleep_desc_layout)

        initViews()

        setChart()

    }


    private fun initViews(){
        pieChart = findViewById(R.id.sleepDialogChart)
        scheduleView = findViewById(R.id.sleepDialogScheduleView)

        dialogSleepOkTv.setOnClickListener{
            dismiss()
        }
    }

    fun setSleepModel(sleepModel: SleepModel){
        this.sleepModel = sleepModel
        Timber.e("------sleep="+Gson().toJson(sleepModel))
        scheduleView?.setSleepData(sleepModel)
        if(sleepModel .countSleepTime == 0){
            setData(mutableListOf(),0f,true)
        }else{
            setData(mutableListOf(sleepModel.deepTime,sleepModel.lightTime,sleepModel.awakeTime),sleepModel.countSleepTime.toFloat(),false)
        }

    }


    private fun setChart(){
        pieChart?.setUsePercentValues(true)
        pieChart?.description?.isEnabled = false
        pieChart?.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart?.dragDecelerationFrictionCoef = 0.95f

//        pieChart?.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"))
//        pieChart?.setCenterText(generateCenterSpannableText())

        pieChart?.setExtraOffsets(20f, 0f, 20f, 0f)

        pieChart?.isDrawHoleEnabled = true
        pieChart?.setHoleColor(Color.WHITE)

        pieChart?.setTransparentCircleColor(Color.WHITE)
        pieChart?.setTransparentCircleAlpha(110)

        pieChart?.holeRadius = 58f
        pieChart?.transparentCircleRadius = 61f

        pieChart?.setDrawCenterText(true)

        pieChart?.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        pieChart?.isRotationEnabled = true
        pieChart?.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
     //   pieChart?.setOnChartValueSelectedListener(this)


      //  pieChart?.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
        val l: Legend? = pieChart?.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l?.orientation = Legend.LegendOrientation.VERTICAL
        l?.setDrawInside(false)
        l?.isEnabled = false
    }

    private var parties  = mutableListOf<String>(" "," "," ")

    private val colorArray = mutableListOf<Int>(R.color.sleep_deep_color,R.color.sleep_light_color,R.color.sleep_awake_color)


    private val emptyColor = Color.parseColor("#E8E9ED")
    private fun setData(count: List<Int>, range: Float,isEmpty : Boolean) {
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        if(isEmpty){
            entries.add(PieEntry(100f,""))
            colors.add(emptyColor)
        }else{
            count.forEachIndexed { index, i ->
                if(i != 0){
                    entries.add(PieEntry(i.toFloat()+range/3,parties.get(index)))
                    colors.add(context.resources.getColor(colorArray[index]))
                }

            }
        }

//        for (i in 0 until count) {
//            entries.add(
//                PieEntry(
//                    (Math.random() * range).toFloat() + range / 5,
//                    parties.get(i % parties.size)
//                )
//            )
//        colors.add(context.resources.getColor(colorArray[i]))
//
//        }
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
        data.setDrawValues(!isEmpty)
     //   data.setValueTypeface(tf)
        pieChart?.data = data

        // undo all highlights
        pieChart?.highlightValues(null)
        pieChart?.invalidate()
    }

}