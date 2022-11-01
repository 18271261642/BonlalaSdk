package com.bonlala.fitalent.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.OneDayStepModel
import com.bonlala.fitalent.db.model.StepItem
import com.bonlala.fitalent.emu.DbType
import com.bonlala.fitalent.utils.BikeUtils
import com.google.gson.Gson
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/8
 */
class HistoryStepViewModel : ViewModel() {


    //获取一整天的计步数据
    var onDayStepStr  = MutableLiveData<OneDayStepModel>()

    //查询一个月的数据，不够补0
    var oneMonthStepList = MutableLiveData<OneDayStepModel>()


    //查询一个自然周的数据，不够补0
    val onWeekStepList = MutableLiveData<OneDayStepModel>()


    //查询一年的数据，12个月，不够补0
    val oneYearStepData = MutableLiveData<OneDayStepModel>()


    //查询所有的计步记录
    val allStepRecord = MutableLiveData<List<String>>()

    //获取一整天的数据
    fun getOnDayStepByDay(dayStr : String ,mac : String){
        val onDayStr = DBManager.getInstance().hasOnDayDetailStep("user_1001",mac,dayStr.trim())
        Timber.e("------计步="+onDayStr+" "+dayStr)
        onDayStepStr.postValue(onDayStr)

    }


    /**
     * 获取自然月的数据
     * month yyyy-MM格式日期
     */
    fun getOneMonthStep(month : String,mac: String){
        //当月的第一天
        val firstMonthDay = BikeUtils.getMonthFirstOrLastDay(month,true)
        //当月的最后一天
        val lastMonthDay = BikeUtils.getMonthFirstOrLastDay(month,false)
        val listStep = DBManager.getInstance().getStartAndEndTimeStepData("user_1001",mac,firstMonthDay,
            lastMonthDay)
        //当月的最后一天日 eg 10月最后一天是31
        val lastDay = BikeUtils.getMonthLastDay(month,false)
        val listStepItem = ArrayList<StepItem>()

        var countDistance  = 0
        var countKcal = 0
        var countStep = 0

        for(i in 0 until lastDay){
            //日期 yyyy-MM-dd格式
            val tempDayStr = BikeUtils.getBeforeOrAfterDay(firstMonthDay,i)
            val stepItem = StepItem()
            var stepValue = 0
            listStep?.forEachIndexed { index, oneDayStepModel ->
                val isEqual = tempDayStr == BikeUtils.transToDate(oneDayStepModel.dayStr)
                if(isEqual){
                    stepValue = oneDayStepModel.dayStep
                    countDistance+=oneDayStepModel.dayDistance
                    countKcal+=oneDayStepModel.dayCalories
                    countStep+=oneDayStepModel.dayStep
                }
            }

            stepItem.hour = i+1
            stepItem.step = stepValue
            listStepItem.add(stepItem)
        }

        val oneDayB = OneDayStepModel()
        oneDayB.detailStep = Gson().toJson(listStepItem)
        oneDayB.dayDistance = countDistance
        oneDayB.dayCalories = countKcal
        oneDayB.dayStep = countStep
        oneMonthStepList.postValue(oneDayB)

    }



    //获取自然周的数据
    fun getOneWeekStepData(context : Context,mac: String, dayStr : String){
        val weekResource = mutableListOf<String>(context.resources.getString(R.string.sun),context.resources.getString(R.string.mon),context.resources.getString(R.string.tue),
            context.resources.getString(R.string.wed),context.resources.getString(R.string.thu),context.resources.getString(R.string.fri),
            context.resources.getString(R.string.sat))
        //周的第一天
        val calendar = BikeUtils.getDayCalendar(dayStr)
        val weekFirstDay = BikeUtils.getWeekFirstDateStr(calendar)
        //最后一天
        val weekLastDay = BikeUtils.getWeekLastDateStr(calendar)

        val weekList = DBManager.getInstance().getStartAndEndTimeStepData("user_1001",mac,weekFirstDay,weekLastDay)
        Timber.e("----周="+weekFirstDay+" "+weekLastDay+" "+Gson().toJson(weekList))
        val stepIteList = mutableListOf<StepItem>()
        var countDistance  = 0
        var countKcal = 0
        var countStep = 0

        for(i in 0 until 7){
            val tempDayStr = BikeUtils.getBeforeOrAfterDay(weekFirstDay,i)
            val stepItem = StepItem()
            stepItem.weekXStr = weekResource[i]

            var step = 0
            if(weekList != null){
                weekList.forEachIndexed { index, oneDayStepModel ->
                    val isEqual = tempDayStr == BikeUtils.transToDate(oneDayStepModel.dayStr)
                    if(isEqual){
                        countDistance = oneDayStepModel.dayDistance
                        countKcal = oneDayStepModel.dayCalories
                        step = oneDayStepModel.dayStep
                        countStep+=oneDayStepModel.dayStep
                    }
                }
            }
            stepItem.step = step
            stepIteList.add(stepItem)
        }
        val oneDayB = OneDayStepModel()
        oneDayB.detailStep = Gson().toJson(stepIteList)
        oneDayB.dayDistance = countDistance
        oneDayB.dayCalories = countKcal
        oneDayB.dayStep = countStep
        onWeekStepList.postValue(oneDayB)
    }



    //查询一年的数据
    fun getOneYearStep(context: Context,mac: String, dayStr : String){
        val yearStr = BikeUtils.getDayOfYear(dayStr)
        val list = mutableListOf<StepItem>()

        var yearCountStep = 0
        var yearCountDistance = 0
        var yearCountKcal = 0

        for(i in 1..12){
            val monthResource = String.format(context.resources.getString(R.string.string_number_month),i)
            val tempMonth = yearStr.toString()+"-"+String.format("%02d",i)

            Timber.e("---mmm="+monthResource)

            val monthData = getOneMonthStepBack(tempMonth,mac)

            val stepItem = StepItem()
            stepItem.step = monthData.dayStep
            stepItem.weekXStr = monthResource
            yearCountStep+=monthData.dayStep
            yearCountDistance+=monthData.dayDistance
            yearCountKcal+=monthData.dayCalories
            list.add(stepItem)
        }

        val yearStepB = OneDayStepModel()
        yearStepB.dayStep = yearCountStep
        yearStepB.dayDistance = yearCountDistance
        yearStepB.dayCalories = yearCountKcal
        yearStepB.detailStep = Gson().toJson(list)

        oneYearStepData.postValue(yearStepB)
    }


    /**
     * 获取自然月的数据
     * month yyyy-MM格式日期
     */
    fun getOneMonthStepBack(month : String,mac: String) : OneDayStepModel{
        //当月的第一天
        val firstMonthDay = BikeUtils.getMonthFirstOrLastDay(month,true)
        //当月的最后一天
        val lastMonthDay = BikeUtils.getMonthFirstOrLastDay(month,false)
        val listStep = DBManager.getInstance().getStartAndEndTimeStepData("user_1001",mac,firstMonthDay,
            lastMonthDay)
        //当月的最后一天日 eg 10月最后一天是31
        val lastDay = BikeUtils.getMonthLastDay(month,false)
        val listStepItem = ArrayList<StepItem>()

        var countDistance  = 0
        var countKcal = 0
        var countStep = 0

        for(i in 0 until lastDay){
            //日期 yyyy-MM-dd格式
            val tempDayStr = BikeUtils.getBeforeOrAfterDay(firstMonthDay,i)
            listStep?.forEachIndexed { index, oneDayStepModel ->
                val isEqual = tempDayStr == BikeUtils.transToDate(oneDayStepModel.dayStr)
                if(isEqual){
                    countStep += oneDayStepModel.dayStep
                    countDistance+=oneDayStepModel.dayDistance
                    countKcal+=oneDayStepModel.dayCalories

                }
            }
        }

        val oneDayB = OneDayStepModel()
        oneDayB.detailStep = Gson().toJson(listStepItem)
        oneDayB.dayDistance = countDistance
        oneDayB.dayCalories = countKcal
        oneDayB.dayStep = countStep
        return oneDayB

    }


    //获取所有的记录数据
    fun getAllStepRecord(mac: String){
        val stepRecord = DBManager.getInstance().getAllRecordByType("user_1001",mac,DbType.DB_TYPE_STEP)
        if(stepRecord != null){
            allStepRecord.postValue(stepRecord)
        }
    }
}