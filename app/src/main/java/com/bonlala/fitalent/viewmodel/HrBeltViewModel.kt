package com.bonlala.fitalent.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.bean.ExerciseHomeBean
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.emu.DbType
import com.bonlala.fitalent.ui.home.HomeViewModel
import com.bonlala.fitalent.utils.BikeUtils
import timber.log.Timber

/**
 * 心率带的viewModel
 * Created by Admin
 *Date 2022/12/16
 */
class HrBeltViewModel :ViewModel(){

    var todayExercise = MutableLiveData<ExerciseHomeBean?>()

    /**获取当天的锻炼数据**/
    fun getTodayExerciseData(mac: String,context: Context){
        val allDbTime = DBManager.getInstance().getLastDayOfType("user_1001",mac, DbType.DB_TYPE_EXERCISE)
         Timber.e("---dddadfsd="+allDbTime)
        if(allDbTime == null){
            todayExercise.postValue(null)
            return
        }

        val todayExerciseList = DBManager.getInstance().getDayExercise("user_1001",mac,allDbTime)
            ?: return

        // Timber.e("-----最近锻炼="+Gson().toJson(todayExerciseList))
        var totalTime = 0
        var totalKcal = 0

        //平均心率的总心率
        var countHeart = 0

        todayExerciseList.forEach {
            totalTime += it.exerciseMinute
            totalKcal += it.kcal
            if(it.avgHr != 0){
                countHeart += it.avgHr
            }
        }

        //平均心率
        val avgHr = countHeart / todayExerciseList.size

        //时间秒，格式化成HH:mm:ss格式
        val timeStr = BikeUtils.formatMinuteStr(totalTime,context)

        val exerciseHomeBean = ExerciseHomeBean(avgHr,totalKcal,timeStr,todayExerciseList.size,allDbTime)

        todayExercise.postValue(exerciseHomeBean)
    }


}