package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.OneDayHeartModel
import com.bonlala.fitalent.emu.DbType

/**
 * 心率的viewModel
 * Created by Admin
 *Date 2022/10/13
 */
class HistoryHeartViewModel : ViewModel() {

    //查询一天的心率数据
    var oneDayHeart = MutableLiveData<OneDayHeartModel>()

    //查询日期的记录
    var hrRecordList = MutableLiveData<List<String>>()

    fun queryOneDayHeart(mac : String,dayStr : String){
        val htBean = DBManager.getInstance().queryOnDayHeart("user_1001",mac,dayStr)
        oneDayHeart.postValue(htBean)
    }


    fun getAllHrRecord(mac: String){
        val list = DBManager.dbManager.getAllRecordByType("user_1001",mac,DbType.DB_TYPE_DETAIL_HR)
        if(list != null){
            val hrList = mutableListOf<String>()
            list.forEach {
                hrList.add(it)
            }

            hrRecordList.postValue(hrList)
        }
    }
}