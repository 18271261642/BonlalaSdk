package com.bonlala.fitalent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blala.blalable.BleOperateManager
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.bean.HomeHeartBean
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.*
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.DbType
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.viewmodel.HistorySleepViewModel
import com.google.gson.Gson
import timber.log.Timber

class HomeViewModel : HistorySleepViewModel() {

    private val userId = "user_1001"

    //测量的心率数据
     var singleHt = MutableLiveData<SingleHeartModel>()
    //测量的血压数据
     var singleBp = MutableLiveData<SingleBpModel>()
    //测量的血氧数据
     var singleSpo2 = MutableLiveData<SingleSpo2Model>()

    //连续的心率
    var detailHr = MutableLiveData<OneDayHeartModel>()

    //心率 单次+连续
    var homeHr = MutableLiveData<HomeHeartBean>()


    /**
     *  获取当天的锻炼数据
     *  时长+卡路里 = time+"+"kcal eg "20+20"
     */

    var todayExercise = MutableLiveData<Map<String,String>?>()

    //查询最近一条的测量心率数据
    fun queryLastSingleHeart(mac : String){
        val listHt = DBManager.getInstance().querySingleHeart(userId,mac)

        singleHt.postValue(listHt?.get(listHt.size-1))
    }

    //查询最近一次的血压
    fun queryLastSingleBp(mac : String){
        val listBp = DBManager.getInstance().querySingleBp(userId,mac)
        Timber.e("----sbbbbb="+Gson().toJson(listBp))
        singleBp.postValue(listBp?.get(listBp.size-1))
    }


    //查询最近一次的血氧
    fun queryLastSingleSpo2(mac : String){
        val listSpo2 = DBManager.getInstance().querySingleSpo2(userId,mac)
        singleSpo2.postValue(listSpo2?.get(listSpo2.size-1))
    }


    //查询最近查一次的详细的心率
    fun queryLastDetailHr(mac: String){

        val lastDayHr = DBManager.getInstance().getLastDayOfType(userId,mac,DbType.DB_TYPE_DETAIL_HR)
        Timber.e("----最近一次心率="+lastDayHr)
        if(lastDayHr != null){
            val detailHrList = DBManager.dbManager.queryOnDayHeart(userId,mac,lastDayHr);
            if(detailHrList != null){
                detailHrList
                detailHr.postValue(detailHrList)
            }
        }
    }


    //查询单次和连续心率
    fun queryHrData(mac: String){
        val listHt = DBManager.getInstance().querySingleHeart(userId,mac)
        val homeHeartBean = HomeHeartBean()
        if(listHt != null){
            val lastHr = listHt[listHt.size-1]
            homeHeartBean.singleHr = lastHr.heartValue
            homeHeartBean.singleHrTime = lastHr.saveLongTime
        }
        val detailHrList = DBManager.dbManager.queryLastTenHr(userId,mac);
        if(detailHrList != null){
            homeHeartBean.hrList = detailHrList.heartList
            homeHeartBean.detailHrTime = detailHrList.saveLongTime
        }
        homeHr.postValue(homeHeartBean)
    }



    /**获取当天的锻炼数据**/
    fun getTodayExerciseData(mac: String){
        val allDbTime = DBManager.getInstance().getLastDayOfType(userId,mac,DbType.DB_TYPE_EXERCISE)
        Timber.e("---dddadfsd="+allDbTime)
        if(allDbTime == null){
            todayExercise.postValue(null)
            return
        }

        val todayExerciseList = DBManager.getInstance().getDayExercise(userId,mac,allDbTime)
            ?: return

        Timber.e("-----最近锻炼="+Gson().toJson(todayExerciseList))
        var totalTime = 0
        var totalKcal = 0
        todayExerciseList.forEach {
            totalTime += it.exerciseTime
            totalKcal += it.kcal
        }

        val str = "$totalTime+$totalKcal"
        val map = HashMap<String,String>()
        map.put(allDbTime,str)
        todayExercise.postValue(map)
    }





    private var users: MutableLiveData<List<String>>? = null
    fun getUsers(): LiveData<List<String>>? {
        if (users == null) {
            users = MutableLiveData<List<String>>()
            loadUsers()
        }
        return users
    }

    private fun loadUsers() {
        val arrML = mutableListOf<String>()
        for(i in 1..10){
            arrML.add("test$i")
        }
        users?.postValue(arrML)
    }



    //设置实时心率开关
    fun setRealHeartStatus(bleOperateManager: BleOperateManager, isOpen : Boolean){
        if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED){
            bleOperateManager.setHeartStatus(isOpen){

            }
        }

    }





    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}


