package com.bonlala.fitalent.activity


import android.view.View
import com.blala.blalable.BleOperateManager
import com.blala.blalable.blebean.CommTimeBean
import com.blala.blalable.listener.OnCommTimeSetListener
import com.bonlala.action.AppActivity
import com.bonlala.base.BaseDialog
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DeviceSetModel
import com.bonlala.fitalent.dialog.TimeDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.utils.MmkvUtils
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_trun_wrist_layout.*

/**
 * 勿扰模式
 * Created by Admin
 *Date 2022/9/5
 */
class DNTActivity : AppActivity(),View.OnClickListener{


    var timeBean : CommTimeBean?= null
    var deviceSetModel : DeviceSetModel ?= null

    override fun getLayoutId(): Int {
      return R.layout.activity_trun_wrist_layout
    }

    override fun initView() {
        title = resources.getString(R.string.string_dnt)
        turnWristSubTv.setOnClickListener(this)
        turnWristStartTimeBar.setOnClickListener(this)
        turnWristEndTimeBar.setOnClickListener(this)
        turnWristSwitchBtn.setOnCheckedChangeListener { button, checked ->
            turnWristSwitchBtn.isChecked = checked
            timeBean?.switchStatus = if(checked) 1 else 0

            contentLayout.visibility = if(checked) View.VISIBLE else View.GONE
            setDntData()
        }
        alertDescTv.text = resources.getString(R.string.string_dnt_desc)
    }

    override fun initData() {
        if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED)
            return
        val mac = MmkvUtils.getConnDeviceMac()
        deviceSetModel = DBManager.getInstance().getDeviceSetModel("user_1001",mac)
        BaseApplication.getInstance().bleOperate.getDNTStatus(OnCommTimeSetListener {

            timeBean = CommTimeBean()
            timeBean?.startHour = it.startHour
            timeBean?.startMinute = it.startMinute
            timeBean?.endHour = it.endHour
            timeBean?.endMinute = it.endMinute
            timeBean?.switchStatus = it.switchStatus

            contentLayout.visibility = if(it.switchStatus == 1) View.VISIBLE else View.GONE

            turnWristSwitchBtn.isChecked = it.switchStatus == 1
            turnWristStartTimeBar.rightText = String.format("%02d",it.startHour)+":"+String.format("%02d",it.startMinute)
            turnWristEndTimeBar.rightText = String.format("%02d",it.endHour)+":"+String.format("%02d",it.endMinute)

            //判断结束时间是否小于开始时间
            val startTime = it.startHour*60+it.startMinute
            val endTime = it.endHour * 60 + it.endMinute
            val endStr = String.format("%02d",it.endHour)+":"+String.format("%02d",it.endMinute)
            deviceSetModel?.dnt= if(it.switchStatus == 0) "0" else  ( String.format("%02d",it.startHour)+":"+String.format("%02d",it.startMinute)+"-"+if(endTime<startTime) resources.getString(R.string.string_next_day)+endStr else endStr)
            setDntData()

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
                    //判断结束时间是否小于开始时间
                    val startTime = (timeBean?.startHour ?: 0) *60+ (timeBean?.startMinute ?: 0)
                    val endTime = (timeBean?.endHour ?: 0) * 60 + (timeBean?.endMinute ?: 0)
                    val endStr = String.format("%02d",timeBean?.endHour)+":"+String.format("%02d",timeBean?.endMinute)
                    deviceSetModel?.dnt= if(timeBean?.switchStatus == 0) "0" else  ( String.format("%02d",timeBean?.startHour)+":"+String.format("%02d",timeBean?.startMinute)+"-"+if(endTime<startTime) resources.getString(R.string.string_next_day)+endStr else endStr)
                    setDntData()
                }

                override fun onClickRepeatClick() {

                }

            })

            .create().show()
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when(p0.id){
                R.id.turnWristStartTimeBar->{   //开始时间
                    showDialogSelect(0)
                }
                R.id.turnWristEndTimeBar->{ //结束时间
                    showDialogSelect(1)
                }
                R.id.turnWristSubTv->{
                    setDntData()
                }
            }
        }
    }

    private fun setDntData(){
        saveData()
        if(timeBean == null)
            return
        BaseApplication.getInstance().bleOperate.setDNTStatus(timeBean){
            deviceSetModel?.dnt= if(timeBean?.switchStatus == 0) "0" else  String.format("%02d",timeBean?.startHour)+":"+String.format("%02d",timeBean?.startMinute)+"-"+String.format("%02d",timeBean?.endHour)+":"+String.format("%02d",timeBean?.endMinute)
            BleOperateManager.getInstance().setClearListener()

            ToastUtils.show("设置成功!")
        }


    }

    private fun saveData(){
        if(deviceSetModel == null)
            return
        val mac = MmkvUtils.getConnDeviceMac() ?: return
        DBManager.dbManager.saveDeviceSetData("user_1001",mac,deviceSetModel)
    }
}