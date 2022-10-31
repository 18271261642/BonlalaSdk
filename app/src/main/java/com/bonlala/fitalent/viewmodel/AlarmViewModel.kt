package com.bonlala.fitalent.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blala.blalable.BleOperateManager
import com.blala.blalable.blebean.AlarmBean
import com.blala.blalable.listener.WriteBackDataListener
import java.util.*

/**
 * Created by Admin
 *Date 2022/9/14
 */
class AlarmViewModel : ViewModel() {


    var alarmTxt = MutableLiveData<String>()

    var alarmList = mutableListOf<AlarmBean>()

    val alarmSource = MutableLiveData<List<AlarmBean>>()

    fun getAlarmData(bleOperateManager: BleOperateManager){
        alarmList.clear()
        readAllAlarm(bleOperateManager,0x00)
    }

    //读取闹钟
    private fun readAllAlarm(bleOperateManager: BleOperateManager,id : Int){
        bleOperateManager.readAlarm(id,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                if(data == null)
                    return
                Log.e("闹钟","-----data="+Arrays.toString(data))
                if(data[0].toInt() == 18 && data[1].toInt() == -1){
                    if(data[3].toInt() == 0x00){    //第一条闹钟
                        val alarmB = bleOperateManager.analysisAlarm(data)
                        alarmList.add(alarmB)
                        readAllAlarm(bleOperateManager,id+1)
                    }

                    if(data[3].toInt() == 0x01){    //第二条闹钟
                        val alarmB = bleOperateManager.analysisAlarm(data)
                        alarmList.add(alarmB)
                        readAllAlarm(bleOperateManager,id+1)
                    }
                    if(data[3].toInt() == 0x02){    //第三条闹钟
                        val alarmB = bleOperateManager.analysisAlarm(data)
                        alarmList.add(alarmB)
                        alarmSource.postValue(alarmList)
                    }
                }
            }

        })
    }


    //保存闹钟，按下标修改
     fun updateItemAlarm(bleOperateManager: BleOperateManager,alarmBean: AlarmBean){
        bleOperateManager.setAlarmId(alarmBean){

        }
    }
}