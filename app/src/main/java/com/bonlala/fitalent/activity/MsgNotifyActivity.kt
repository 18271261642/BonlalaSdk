package com.bonlala.fitalent.activity

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.recyclerview.widget.LinearLayoutManager
import com.blala.blalable.BleOperateManager
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.AppNotifyAdapter
import com.bonlala.fitalent.bean.NotifyBean
import com.bonlala.fitalent.ble.DataOperateManager
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DeviceSetModel
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import kotlinx.android.synthetic.main.activity_msg_notify_layout.*
import timber.log.Timber

/**
 * 消息提醒页面
 * Created by Admin
 *Date 2022/10/31
 */
class MsgNotifyActivity : AppActivity(){

    private var adapter : AppNotifyAdapter ?= null
    private var list : MutableList<NotifyBean> ?= null

    //开关
    private var isAllOpen : Boolean ?= null
    private var deviceSet : DeviceSetModel ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_msg_notify_layout
    }

    override fun initView() {

        notifyOpenNotifyLayout.setOnClickListener {
            openNotify()
        }

        notifyAllSwitchBtn.setOnCheckedChangeListener { button, checked ->
            Timber.e("---button="+(button.isPressed))

            dealStatus(checked)
            val mac = MmkvUtils.getConnDeviceMac()
            if(BikeUtils.isEmpty(mac))
                return@setOnCheckedChangeListener
            deviceSet?.isAllAppMsgStatus = checked
            DBManager.getInstance().saveDeviceSetData("user_1001",mac,deviceSet)
        }


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        notifyRecyclerView.layoutManager = linearLayoutManager
        list = mutableListOf()
        adapter = AppNotifyAdapter(this,list)
        notifyRecyclerView.adapter = adapter
        list!!.clear()
        list!!.addAll(dealAppTypes())
        adapter!!.notifyDataSetChanged()

        var contentStr = contentTv.text.toString()
        var inputStr = notifyTypeEdit.text

//        if(BikeUtils.isEmpty(inputStr) || BikeUtils.isEmpty(contentStr)){
//            return
//        }
        var cont = 0
        testNotifyBtn.setOnClickListener {
            BleOperateManager.getInstance().setClearListener()
//            BleOperateManager.getInstance().getExerciseData(inputStr.toString().toInt(),object : WriteBackDataListener{
//                override fun backWriteData(data: ByteArray?) {
//                   Timber.e("-----获取锻炼="+Utils.formatBtArrayToString(data))
//                }
//
//            })
            cont++

            BleOperateManager.getInstance().sendAPPNoticeMessage(inputStr.toString().toInt(),"00","00"+cont
            ) {
            }
        }

    }

    override fun initData() {
        val mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac))
            return
        deviceSet = DBManager.dbManager.getDeviceSetModel("user_1001",mac)
        if(deviceSet != null){
            isAllOpen = deviceSet!!.isAllAppMsgStatus
        }
        dealStatus(isAllOpen!!)
    }



    //处理全部的开或关
    private fun dealStatus(isOpen : Boolean){
        list?.forEach {
            it.isChecked = isOpen
        }
        adapter!!.notifyDataSetChanged()
    }


    private fun openNotify(){
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivityForResult(intent, 0x01)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //处理显示的app
    private fun dealAppTypes() : List<NotifyBean>{
        val list = mutableListOf<NotifyBean>()
        val msgBean = NotifyBean(resources.getString(R.string.string_message),R.mipmap.icon_notify_msg)
        list.add(msgBean)
        val mailBean = NotifyBean(resources.getString(R.string.string_mail),R.mipmap.icon_notify_mail)
        list.add(mailBean)
        val wechatB = NotifyBean(resources.getString(R.string.string_wechat),R.mipmap.icon_notify_wechat)
        list.add(wechatB)
        val qqB = NotifyBean("QQ",R.mipmap.icon_notify_qq)
        list.add(qqB)
        list.add(NotifyBean("WhatsApp",R.mipmap.icon_notify_whatapp))
        list.add(NotifyBean("Facebook",R.mipmap.icon_notify_facebook))
        list.add(NotifyBean("Twitter",R.mipmap.icon_notify_twitter))
        list.add(NotifyBean("LinkedIn",R.mipmap.icon_notify_linked))
        list.add(NotifyBean("Instagram",R.mipmap.icon_notify_instagram))
        list.add(NotifyBean("Snapchat",R.mipmap.icon_notify_snapchat))
        list.add(NotifyBean("LINE",R.mipmap.icon_notify_line))
        list.add(NotifyBean("Kakao Talk",R.mipmap.icon_notify_kakaotalk))
        list.add(NotifyBean("Viber",R.mipmap.icon_notify_viber))
        list.add(NotifyBean("Skype",R.mipmap.icon_notify_skype))
        list.add(NotifyBean("Telegram",R.mipmap.icon_notify_telegram))
        list.add(NotifyBean("tikTok",R.mipmap.icon_notify_titolk))
        return list

    }
}