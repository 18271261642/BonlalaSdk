package com.bonlala.fitalent.activity

import android.view.View
import com.blala.blalable.blebean.CommTimeBean
import com.blala.blalable.listener.OnCommTimeSetListener
import com.bonlala.action.AppActivity
import com.bonlala.base.BaseDialog
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DeviceSetModel
import com.bonlala.fitalent.dialog.HeightSelectDialog
import com.bonlala.fitalent.dialog.TimeDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.utils.MmkvUtils
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_trun_wrist_layout.*
import timber.log.Timber

/**
 * 久坐提醒
 * Created by Admin
 *Date 2022/9/5
 */
class LongSitActivity : AppActivity(),View.OnClickListener{


    var timeBean : CommTimeBean ?= null
    var deviceSetModel : DeviceSetModel ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_trun_wrist_layout
    }

    override fun initView() {
        turnWristLevelBar.visibility = View.VISIBLE
        commTimeSetSwitchLayout.visibility = View.GONE

        alertDescTv.text =  ""
        title = resources.getString(R.string.string_long_sit)

        turnWristSubTv.setOnClickListener(this)
        turnWristStartTimeBar.setOnClickListener(this)
        turnWristEndTimeBar.setOnClickListener(this)
        turnWristLevelBar.setOnClickListener(this)

//        turnWristSwitchBtn.setOnCheckedChangeListener { button, checked ->
//
//            contentLayout.visibility = if(checked) View.VISIBLE else View.GONE
//            timeBean?.level = if(checked) 60 else 0
//            if(checked && button.isPressed){
//                turnWristLevelBar.rightText =  "${60} "+resources.getString(R.string.string_minute)
//            }
//
//            setLongSitData()
//        }

    }

    override fun initData() {
        if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED)
            return
        val mac = MmkvUtils.getConnDeviceMac()
        deviceSetModel = DBManager.getInstance().getDeviceSetModel("user_1001",mac)
        BaseApplication.getInstance().bleOperate.getLongSitData(OnCommTimeSetListener { commTimeBean ->
            Timber.e("----久坐="+commTimeBean.toString())
            timeBean = commTimeBean

            /**
             * 判断开关是否打开，开关关闭是开始时间00结束时间23:59
             */
            turnWristSwitchBtn.isChecked = commTimeBean.switchStatus == 1
//            contentLayout.visibility = if(commTimeBean.switchStatus == 1) View.VISIBLE else View.GONE


            turnWristStartTimeBar.rightText = String.format("%02d",commTimeBean.startHour)+":"+String.format("%02d",commTimeBean.startMinute)
            turnWristEndTimeBar.rightText = String.format("%02d",commTimeBean.endHour)+":"+String.format("%02d",commTimeBean.endMinute)
            turnWristLevelBar.rightText =  "${commTimeBean.level} "+resources.getString(R.string.string_minute)

            val startT = commTimeBean.startHour*60 + commTimeBean.startMinute
            val endT = commTimeBean.endHour *60 + commTimeBean.endMinute
            val endStr = String.format("%02d",commTimeBean.endHour)+":"+String.format("%02d",commTimeBean.endMinute)
            deviceSetModel?.longSitStr=    String.format("%02d",commTimeBean.startHour)+":"+String.format("%02d",commTimeBean.startMinute)+"-"+(if(startT > endT) resources.getString(R.string.string_next_day)+endStr else endStr)
            saveData()
        })
    }


    private fun showDialogSelect(code : Int){

        var backHour = 0
        var backMinute = 0

        //开始时间
        if(code == 0){
            backHour = timeBean?.startHour ?: 0
            backMinute = timeBean?.startMinute ?: 0
        }else{
            backHour = timeBean?.endHour ?: 0
            backMinute = timeBean?.endMinute ?: 0
        }

        val timeDialog = TimeDialog.Builder(this)
            .setIgnoreSecond()
            .setHour(backHour)
            .setMinute(backMinute)
            .setListener(object : TimeDialog.OnListener{
                override fun onSelected(dialog: BaseDialog?, hour: Int, minute: Int, second: Int) {
                    dialog?.dismiss()
                    val timeStr = String.format("%02d",hour)+":"+String.format("%02d",minute)

                    if(code == 0){
                        turnWristStartTimeBar.rightText = timeStr
                        timeBean?.startHour = hour
                        timeBean?.startMinute = minute
                    }else{
                        turnWristEndTimeBar.rightText = timeStr
                        timeBean?.endHour = hour
                        timeBean?.endMinute = minute
                    }
                    val startM =  (timeBean?.startHour?.times(60) ?: 0) + minute
                    val endM = (timeBean?.endHour?.times(60) ?: 0) + minute

                    val endStr = String.format("%02d",timeBean?.endHour)+":"+String.format("%02d",timeBean?.endMinute)
                    deviceSetModel?.longSitStr=  String.format("%02d",timeBean?.startHour)+":"+String.format("%02d",timeBean?.startMinute)+"-"+(if(endM<startM) resources.getString(R.string.string_next_day) +endStr else endStr)
                    setLongSitData()
                }

                override fun onClickRepeatClick() {

                }
            })
            .create().show()
    }



    //久坐提醒间隔
    private fun showInterval(){
        val list = mutableListOf<String>()
        list.add("15")
        list.add("30")
        list.add("45")
        list.add("60")
        var position = 0
        list.forEachIndexed { index, s ->
            if((timeBean?.level ?: 0) == s.toInt()){
                position = index
            }
        }
        val heightDialog = HeightSelectDialog.Builder(this,list)
            .setUnitShow(true,resources.getString(R.string.string_minute))
            .setDefaultSelect(position)
            .setSignalSelectListener {
                turnWristLevelBar.rightText = it+resources.getString(R.string.string_minute)
                timeBean?.level = it.toInt()
                setLongSitData()
            }
            .show()

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.turnWristStartTimeBar->{   //开始时间
                showDialogSelect(0)
            }
            R.id.turnWristEndTimeBar->{ //结束时间
                showDialogSelect(1)
            }
            R.id.turnWristSubTv->{
                setLongSitData()
            }
            //间隔
            R.id.turnWristLevelBar->{
                showInterval()
            }
        }
    }

    private fun setLongSitData(){
        if(timeBean == null)
            return
        BaseApplication.getInstance().bleOperate.setLongSitData(timeBean){
            deviceSetModel?.longSitStr= if(timeBean?.switchStatus == 0) "0" else  String.format("%02d",timeBean?.startHour)+":"+String.format("%02d",timeBean?.startMinute)+"-"+String.format("%02d",timeBean?.endHour)+":"+String.format("%02d",timeBean?.endMinute)


            ToastUtils.show("设置成功")
            BaseApplication.getInstance().bleOperate.setClearListener()
        }
        saveData()
    }

    private fun saveData(){
        if(deviceSetModel == null)
            return
        val mac = MmkvUtils.getConnDeviceMac() ?: return



        DBManager.dbManager.saveDeviceSetData("user_1001",mac,deviceSetModel)
    }
}