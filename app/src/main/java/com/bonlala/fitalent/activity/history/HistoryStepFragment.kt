package com.bonlala.fitalent.activity.history

import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.viewModels
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.RecordHistoryActivity
import com.bonlala.fitalent.db.model.OneDayStepModel
import com.bonlala.fitalent.db.model.SleepItem
import com.bonlala.fitalent.db.model.StepItem
import com.bonlala.fitalent.dialog.CalendarSelectDialog
import com.bonlala.fitalent.emu.StepType
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.CalculateUtils
import com.bonlala.fitalent.utils.GsonUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.view.StepChartView
import com.bonlala.fitalent.viewmodel.HistoryStepViewModel
import com.github.mikephil.charting.charts.BarChart
import com.google.gson.Gson
import com.haibin.calendarview.CalendarUtil
import kotlinx.android.synthetic.main.common_history_bot_date_layout.*
import kotlinx.android.synthetic.main.fragment_history_step_layout.*
import kotlinx.android.synthetic.main.fragment_history_step_layout.view.*
import timber.log.Timber

/**
 * 步数fragment
 * Created by Admin
 *Date 2022/9/27
 */
class HistoryStepFragment : TitleBarFragment<RecordHistoryActivity>() {

    private val viewModel by viewModels<HistoryStepViewModel>()

    private var stepChart : BarChart ?= null

    private var stepChartView : StepChartView ?= null

    private var dayStr : String ?= null

    //类型 日，周，月，年
    private var dataType =  StepType.DAY

    private val dateList = mutableListOf<String>()


    override fun getLayoutId(): Int {

       return R.layout.fragment_history_step_layout
    }

    override fun initView() {
        stepChart = findViewById(R.id.stepBarChart)
        stepChartView = findViewById(R.id.stepChartView)


        setOnClickListener(R.id.stepDayLayout,R.id.stepWeekLayout,R.id.stepMonthLayout,R.id.stepYearLayout,
        R.id.commonHistoryLeftImg,R.id.commonHistoryRightImg,R.id.commonHistoryCurrentTv,R.id.commonHistoryCalendarImg)
    }

    override fun initData() {

        dayStr = BikeUtils.getCurrDate()
        showResult()

        val mac = MmkvUtils.getConnDeviceMac()
        viewModel.getAllStepRecord(mac)

        //默认日
        showDayCheck(0)
    }

    private fun showResult(){
        val stepGoal = MmkvUtils.getStepGoal()
        //记录的数据
        viewModel.allStepRecord.observe(viewLifecycleOwner){
            Timber.e("------记录的数据="+Gson().toJson(it))
            dateList.clear()
            dateList.addAll(it)
        }


        //日
        viewModel.onDayStepStr.observe(viewLifecycleOwner){
            Timber.e("------一天的计步="+Gson().toJson(it))

            if(it == null){
                showEmptyData()
                return@observe
            }
            showValidData(StepType.DAY,it.detailStep,it.dayStep,it.dayDistance,it.dayCalories)

        }
        //年
        viewModel.oneYearStepData.observe(viewLifecycleOwner){
            val stepStr = it.detailStep
            showStepGoal(stepStr,365,stepGoal)

            showValidData(StepType.YEAR,it.detailStep,it.dayStep,it.dayDistance,it.dayCalories)
        }

        //周
        viewModel.onWeekStepList.observe(viewLifecycleOwner){
            val stepStr = it.detailStep
            showStepGoal(stepStr,7,stepGoal)

            showValidData(StepType.WEEK,it.detailStep,it.dayStep,it.dayDistance,it.dayCalories)
        }
    }


    private fun showStepGoal(stepDetail : String,days: Int,stepGoal : Int){
        val stepList = GsonUtils.getGsonObject<List<StepItem>>(stepDetail)
        var count = 0
        stepList?.forEachIndexed { index, stepItem ->

            if(stepItem.step>=stepGoal){
                count++
            }
        }
        attachActivity.setStepSchedule(count.toFloat(),days.toFloat())
    }



    //日的数据
    private fun getDayData(){
        if(BikeUtils.daySize(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.GONE
            return
        }
        if(!BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.VISIBLE
        }else{
            commonHistoryCurrentTv.visibility = View.GONE
        }

        val mac = MmkvUtils.getConnDeviceMac()
        dayStr?.let { viewModel.getOnDayStepByDay(it,mac) }

    }


    //年的数据
    private fun getYearData(){
        if(BikeUtils.daySize(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.GONE
            return
        }
        if(!BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.VISIBLE
        }else{
            commonHistoryCurrentTv.visibility = View.GONE
        }

        val mac = MmkvUtils.getConnDeviceMac()

        viewModel.getOneYearStep(attachActivity,mac,dayStr.toString())
    }


    //周的数据
    private fun getWeekData(){
        if(BikeUtils.daySize(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.GONE
            return
        }
        if(!BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.VISIBLE
        }else{
            commonHistoryCurrentTv.visibility = View.GONE
        }

        val mac = MmkvUtils.getConnDeviceMac()
        viewModel.getOneWeekStepData(attachActivity, mac,dayStr.toString())
    }


    //展示有效的数据
    private fun showValidData(type : StepType,detailStep : String,dayStep : Int,dayDistance : Int,dayCalories : Int){
        val oneDayStepModel = OneDayStepModel()
        Timber.e("-----有效的数据="+detailStep)
        val stepItemList = GsonUtils.getGsonObject<List<StepItem>>(detailStep)
        val tempList = stepItemList?.reversed()
        (0 until tempList?.size!!).also { range->
            range.forEach fe@{
                if(tempList.get(it).step !=0){
                    tempList.get(it).isChecked = true
                    return@also
                }

            }
        }


        oneDayStepModel.detailStep =Gson().toJson(tempList.reversed())
        oneDayStepModel.dayStr = dayStr
        oneDayStepModel.dayStep = dayStep
        oneDayStepModel.dayDistance = dayDistance
        oneDayStepModel.dayCalories = dayCalories
        stepChartView?.stepType = type
        stepChartView?.oneDayStepModel = oneDayStepModel

        //是否达标
        val stepGoal = MmkvUtils.getStepGoal()
        if(type == StepType.DAY){   //日
            attachActivity.setStepSchedule(oneDayStepModel.dayStep.toFloat(),stepGoal.toFloat())
        }


        //总的计步
        stepTotalStepTv.text = oneDayStepModel.dayStep.toString()+" step"
        //距离
        val distance = oneDayStepModel.dayDistance
        val kmDis = CalculateUtils.mToKm(distance)
        val isKm = MmkvUtils.getUnit()
        stepHistoryDistanceTv.text = if(isKm) kmDis.toString() else CalculateUtils.kmToMiValue(kmDis).toString()
        historyStepUnitTv.text = if(isKm) "km" else "mi"

        stepHistoryKcalTv.text = oneDayStepModel.dayCalories.toString()

        //计算平均步数，日不显示
        if(type != StepType.DAY){
            var countDayStep = 0
            var countDayNumbers = 0
            tempList.reversed().forEach {
                if(it.step != 0){
                    countDayStep+=it.step
                    countDayNumbers++
                }
            }
            if(countDayStep == 0 || countDayStep == 0){
                stepHistoryAvgStepTv.text = "--"
            }else{
                val avgStep = countDayStep / countDayNumbers

                stepHistoryAvgStepTv.text = avgStep.toInt().toString()
            }

        }




        if(type == StepType.WEEK){
            //val weekCalendar = BikeUtils.getDayCalendar(dayStr)
            //周的第一天和最后一天，周日到周六
            val sunDay = BikeUtils.getWeekSunToSta(BikeUtils.transToDate(dayStr))
            commonHistoryDateTv.text = sunDay
        }
//        else if(type == StepType.MONTH){
//            commonHistoryDateTv.text = BikeUtils.getDayByMonth(dayStr)
//        }

        else if(type == StepType.YEAR){
            val yearStr = BikeUtils.getDayOfYear(dayStr)
            commonHistoryDateTv.text = yearStr.toString()
        }
        else{
            commonHistoryDateTv.text = dayStr
        }

    }


    //展示空的数据
    private fun showEmptyData(){
        attachActivity.setStepSchedule(0f,MmkvUtils.getStepGoal().toFloat())
        commonHistoryDateTv.text = dayStr
        //总的计步
        stepTotalStepTv.text = "-- step"
        //距离
        stepHistoryDistanceTv.text = "--"
        stepHistoryKcalTv.text = "--"

        val oneDayStepModel = OneDayStepModel()
        oneDayStepModel.detailStep = emptyHourListData()
        oneDayStepModel.dayStr = dayStr
        oneDayStepModel.dayStep = 0
        oneDayStepModel.dayDistance = 0
        oneDayStepModel.dayCalories = 0
        stepChartView?.stepType = StepType.DAY
        stepChartView?.oneDayStepModel = oneDayStepModel
    }


    //组装月的数据，自然月
    private fun getMonthData(){
        if(BikeUtils.daySize("yyyy-MM",dayStr,BikeUtils.getDayByMonth(BikeUtils.getCurrDate()))){
            commonHistoryCurrentTv.visibility = View.GONE
            return
        }

        if(!BikeUtils.isEqualDay("yyyy-MM",dayStr,BikeUtils.getDayByMonth(BikeUtils.getCurrDate()))){
            commonHistoryCurrentTv.visibility = View.VISIBLE
        }else{
            commonHistoryCurrentTv.visibility = View.GONE
        }
        viewModel.oneMonthStepList.observe(viewLifecycleOwner){
            val stepStr = it.detailStep
            showStepGoal(stepStr,BikeUtils.getMonthLastDay(dayStr,false),MmkvUtils.getStepGoal())
            showValidData(StepType.MONTH,it.detailStep,it.dayStep,it.dayDistance,it.dayCalories)

        }
        viewModel.getOneMonthStep(dayStr.toString(),MmkvUtils.getConnDeviceMac())
    }



    private fun showDayCheck(code : Int){
        stepDayView.visibility = View.INVISIBLE
        stepWeekView.visibility = View.INVISIBLE
        stepMonthView.visibility = View.INVISIBLE
        stepYearView.visibility = View.INVISIBLE

        commonHistoryCalendarImg.visibility = if(code == 0) View.VISIBLE else View.GONE
        dayStr = BikeUtils.getCurrDate()

        if(code == 0){  //日
            stepDayView.visibility = View.VISIBLE
            dataType = StepType.DAY
//            dayStr = BikeUtils.getCurrDate()
//            getDayData()
            stepAvgLayout.visibility = View.GONE
        }
        if(code == 1){  //周
            stepWeekView.visibility = View.VISIBLE
            dataType = StepType.WEEK
//            dayStr = BikeUtils.getCurrDate()
//            getWeekData()
            stepAvgLayout.visibility = View.VISIBLE

        }
        if(code == 2){
            stepMonthView.visibility = View.VISIBLE
            dataType = StepType.MONTH
//            dayStr = BikeUtils.getFormatDate(System.currentTimeMillis(),"yyyy-MM")
//            getMonthData()
            stepAvgLayout.visibility = View.VISIBLE
        }
        if(code == 3){
            stepYearView.visibility = View.VISIBLE
            dataType = StepType.YEAR
        }

        backCurrentDay()
    }


    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id;
        when (id){
            //回到当天
            R.id.commonHistoryCurrentTv->{
                backCurrentDay()
            }

            R.id.stepDayLayout->{
                showDayCheck(0)
            }
            R.id.stepWeekLayout->{
                showDayCheck(1)
            }
            R.id.stepMonthLayout->{
                showDayCheck(2)
            }
            R.id.stepYearLayout->{
                showDayCheck(3)
            }

            //前一天
            R.id.commonHistoryLeftImg->{
                selectDate(true)
            }
            //后一天
            R.id.commonHistoryRightImg->{
                selectDate(false)
            }


            //日历
            R.id.commonHistoryCalendarImg->{
                showCalendar()
            }
        }
    }

    //显示日历
    private fun showCalendar(){
        showCalendarSelectDialog(dateList
        ) { day ->
            hidCalendarDialog()
            dayStr = day
            getDayData()
        }



//
//        val calendarDialog = CalendarSelectDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
//        calendarDialog.show()
//        calendarDialog.showCalendarData()
//        calendarDialog.setCancelable(true)
//        calendarDialog.setMarkCalendarDate(dateList)
//        //calendarDialog.setRebackDay(dayStr)
//        calendarDialog.setOnCalendarSelectListener {
//            calendarDialog.dismiss()
//            dayStr = it
//            getDayData()
//        }
//
//        val windowM = calendarDialog.window?.windowManager
//        val layoutP = calendarDialog?.window?.attributes
//
//        val metrics2: DisplayMetrics = resources.displayMetrics
//        val widthW: Int = metrics2.widthPixels
//        layoutP?.width = widthW
//        layoutP?.height = metrics2.heightPixels
//        layoutP?.x = 40
//        layoutP?.y = 0
//        calendarDialog.window?.attributes = layoutP
    }




    //设置日期，前true或后false
    private fun selectDate(date : Boolean){

        if(dataType == StepType.DAY){
            val timeLong = BikeUtils.getBeforeOrAfterDay(dayStr,date)
            dayStr = BikeUtils.getFormatDate(timeLong,"yyyy-MM-dd")
            getDayData()
        }

        if(dataType == StepType.WEEK){
            val beforeOrAfterDay = BikeUtils.getBeforeOrAfterDay(dayStr,if(date) -7 else 7)
            dayStr = BikeUtils.getFormatDate(beforeOrAfterDay,"yyyy-MM-dd")
            getWeekData()
        }

        if(dataType == StepType.MONTH){
            dayStr = BikeUtils.getNextOrLastMonth(dayStr,date)
            getMonthData()
        }

        if(dataType == StepType.YEAR){
            dayStr = BikeUtils.getPreviewOrNextYear(dayStr,date)
            getYearData()
        }

//        if(!BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate())){
//            commonHistoryCurrentTv.visibility = View.VISIBLE
//        }else{
//            commonHistoryCurrentTv.visibility = View.GONE
//        }
    }

    //回到当天
    private fun backCurrentDay(){
        dayStr = BikeUtils.getCurrDate()
        if(dataType == StepType.DAY){
            getDayData()
        }

        if(dataType == StepType.WEEK){
            getWeekData()
        }

        if(dataType == StepType.MONTH){
            dayStr = BikeUtils.getDayByMonth(dayStr)
            getMonthData()
        }

        if(dataType == StepType.YEAR){
            getYearData()
        }
        commonHistoryCurrentTv.visibility = View.GONE
    }


    //空的天数据，24个小时
    private fun emptyHourListData() : String{
        val emptyStepList = ArrayList<StepItem>()
        for(i in 0..23){
            val stepItem = StepItem(0,i)
            emptyStepList.add(stepItem)
        }
        return Gson().toJson(emptyStepList)
    }


}