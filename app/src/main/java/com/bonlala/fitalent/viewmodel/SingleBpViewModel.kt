package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.bean.ChartBpBean
import com.bonlala.fitalent.db.DBManager

/**
 * Created by Admin
 *Date 2022/10/11
 */
class SingleBpViewModel : ViewModel() {

    //所有的血压数据，测量的单次

    var allSingleBpList = MutableLiveData<List<ChartBpBean>?>()

    fun getAllDbSingleBp(userId : String,mac : String){
        val list = DBManager.dbManager.querySingleBp(userId,mac)
        if(list == null){
            allSingleBpList.postValue(null)
            return
        }
        val chartList = mutableListOf<ChartBpBean>()
        list.forEach {
            val chartBpBean = ChartBpBean(it.sysBp,it.diastolicBp,it.saveLongTime.toString())
            chartList.add(chartBpBean)
        }

        allSingleBpList.postValue(chartList)
    }
}