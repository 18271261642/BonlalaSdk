package com.bonlala.fitalent.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.utils.HeartRateConvertUtils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.dialog_hr_desc_layout.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Admin
 *Date 2022/10/14
 */
class HistoryHrDescDialog : AppCompatDialog {

    constructor(context: Context) :super (context){

    }

    constructor(context: Context,theme : Int) : super(context, theme){

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_hr_desc_layout)


        dialogHrOkTv.setOnClickListener {
            dismiss()
        }

        initViews()
    }


    private fun initViews(){
        setChart()
    }


    fun setHrStrongData(hrList : List<Int>){
        var point1 = 0
        var point2 = 0
        var point3 = 0
        var point4 = 0
        var point5 = 0
        var point6 = 0
        hrList.forEachIndexed { index, i ->

            val point = HeartRateConvertUtils.hearRate2Point(i)
            if(point == 0){
                point1++
            }
            if(point == 1){
                point2++
            }

            if(point == 2){
                point3++
            }
            if(point == 3){
                point4++
            }
            if(point == 4){
                point5++
            }
            if(point == 5){
                point6++
            }
        }

        val dataList = mutableListOf<Int>()
        dataList.add(point1)
        dataList.add(point2)
        dataList.add(point3)
        dataList.add(point4)
        dataList.add(point5)
        dataList.add(point6)



        if(Collections.max(dataList) ==0 && Collections.min(dataList) == 0){
            hrDescView.setEmptyData()
            setData(dataList,hrList.size.toFloat(),true)
        }else{
            setData(dataList,hrList.size.toFloat(),false)
            hrDescView.setScheduleData(dataList,hrList.size)
        }


    }

    private fun setChart(){
        hrDialogChart?.setUsePercentValues(true)
        hrDialogChart?.description?.isEnabled = false
        hrDialogChart?.setExtraOffsets(5f, 10f, 5f, 5f)

        hrDialogChart?.dragDecelerationFrictionCoef = 0.95f

//        pieChart?.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"))
//        pieChart?.setCenterText(generateCenterSpannableText())

        hrDialogChart?.setExtraOffsets(20f, 0f, 20f, 0f)

        hrDialogChart?.isDrawHoleEnabled = true
        hrDialogChart?.setHoleColor(Color.WHITE)

        hrDialogChart?.setTransparentCircleColor(Color.WHITE)
        hrDialogChart?.setTransparentCircleAlpha(110)

        hrDialogChart?.holeRadius = 58f
        hrDialogChart?.transparentCircleRadius = 61f

        hrDialogChart?.setDrawCenterText(true)

        hrDialogChart?.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        hrDialogChart?.isRotationEnabled = true
        hrDialogChart?.isHighlightPerTapEnabled = true

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
        val l: Legend? = hrDialogChart?.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l?.orientation = Legend.LegendOrientation.VERTICAL
        l?.setDrawInside(false)
        l?.isEnabled = false
    }

    private var parties  = mutableListOf<String>(" "," "," ","","","")

    private val colorArray = mutableListOf<Int>(R.color.hr_color_1,R.color.hr_color_2,R.color.hr_color_3,R.color.hr_color_4,R.color.hr_color_5,R.color.hr_color_6)


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
        hrDialogChart?.data = data

        // undo all highlights
        hrDialogChart?.highlightValues(null)
        hrDialogChart?.invalidate()
    }
}