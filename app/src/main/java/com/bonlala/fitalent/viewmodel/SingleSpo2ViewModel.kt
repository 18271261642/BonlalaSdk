package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.bean.ChartSpo2Bean
import com.bonlala.fitalent.db.DBManager
import com.google.gson.Gson
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/11
 */
class SingleSpo2ViewModel : ViewModel() {

    val allSingleSpo2List = MutableLiveData<List<ChartSpo2Bean>?>()

    //获取所有的血氧
    fun getAllDbSpo2(userId : String,mac : String){
        val list = DBManager.dbManager.querySingleSpo2(userId,mac)
        Timber.e("----血氧="+Gson().toJson(list))
        val chartList = ArrayList<ChartSpo2Bean>()

        if(list == null){
            allSingleSpo2List.postValue(null)
            return
        }

        list.forEachIndexed { index, singleSpo2Model ->
            val chartSpo2Bean = ChartSpo2Bean(singleSpo2Model.spo2Value,singleSpo2Model.saveLongTime.toString())

            chartList.add(chartSpo2Bean)
        }

        allSingleSpo2List.postValue(chartList)
    }
}