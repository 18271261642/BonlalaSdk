package com.bonlala.fitalent.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.text.format.DateFormat
import com.blala.blalable.BleOperateManager
import com.blala.blalable.blebean.CommBleSetBean
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import timber.log.Timber

/**
 * 获取系统的变化广播
 * 语言
 * 时间格式
 * Created by Admin
 *Date 2022/11/16
 */
class SystemStatusBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        Timber.e("------action="+action)
        //系统语言变化
        if(action == Intent.ACTION_LOCALE_CHANGED){
            if (p0 != null) {
                dealChange(p0)
            }
        }
    }


    private fun dealChange(context: Context){
        val commBleSetBean = CommBleSetBean()
        val mac = MmkvUtils.getConnDeviceMac()
        if (!BikeUtils.isEmpty(mac)) {
            val deviceSetModel = DBManager.dbManager.getDeviceSetModel("user_1001", mac)
            if (deviceSetModel != null) {
                commBleSetBean.is24Heart = if (deviceSetModel.isIs24Heart) 0 else 1
                commBleSetBean.metric = deviceSetModel.isKmUnit
            }
        }



        commBleSetBean.timeType = if (DateFormat.is24HourFormat(context)) 1 else 0
        commBleSetBean.language = if (BaseApplication.getInstance().isChinese) 1 else 0
        BleOperateManager.getInstance().setCommonSetting(commBleSetBean
        ) {

        }
    }
}