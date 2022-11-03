package com.bonlala.fitalent.activity

import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.Utils
import com.blala.blalable.blebean.AlarmBean
import com.bonlala.action.AppActivity
import com.bonlala.base.BaseDialog
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.AlarmAdapter
import com.bonlala.fitalent.dialog.SelectDialog
import com.bonlala.fitalent.dialog.TimeDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.viewmodel.AlarmViewModel
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_alarm_list_layout.*
import timber.log.Timber
import java.util.*
import kotlin.experimental.and


/**
 * 展示闹钟列表页面
 * Created by Admin
 *Date 2022/9/14
 */
class AlarmListActivity : AppActivity() {


    private val viewModel by viewModels<AlarmViewModel>()

    private var alarmList = mutableListOf<AlarmBean>()
    private var adapter : AlarmAdapter ?= null

    private  var timeDialog : TimeDialog.Builder ?= null

    //用于重复周期的判断，
    private var weekRepeatList = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0)

    override fun getLayoutId(): Int {
        return R.layout.activity_alarm_list_layout
    }

    override fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        adapter = AlarmAdapter(this)
        adapter!!.setOnItemClickListener(onItemClick)
        //adapter!!.setOnChildClickListener()
        alarmRecycler.adapter = adapter


    }


    private val onItemClick : com.bonlala.base.BaseAdapter.OnItemClickListener = object : com.bonlala.base.BaseAdapter.OnItemClickListener{
        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            showUpdateItemData(position)
        }

    }

    override fun initData() {
        if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED){
            ToastUtils.show(resources.getString(R.string.string_not_connect))
            return
        }
        showDialog("Loading..")
        viewModel.alarmSource.observe(this){
            hideDialog()
            Log.e("AA","----闹钟="+Gson().toJson(it))
            alarmList.clear()
            alarmList.addAll(it)
            adapter?.data = alarmList

        }
        viewModel.getAlarmData(BaseApplication.getInstance().bleOperate)
    }


    private fun showUpdateItemData(position : Int){
        showDialogSelect(position)
    }

    private fun showDialogSelect(code : Int){
        timeDialog = TimeDialog.Builder(this)
            .setIgnoreSecond()
            .isShowRepeat(true)
            .setTitle(resources.getString(R.string.string_alarm))
            .setWeekRepestValue(alarmList.get(code).repeat.toInt())
            .setChooseRepeat(getRepeat(alarmList.get(code).repeat))
            .setHour(alarmList.get(code).hour)
            .setMinute(alarmList.get(code).minute)
            .setListener(object : TimeDialog.OnListener{
                override fun onSelected(dialog: BaseDialog?, hour: Int, minute: Int, second: Int) {
                    dialog?.dismiss()
                    val timeStr = String.format("%02d",hour)+":"+String.format("%02d",minute)
                    adapter?.getItem(code)?.hour   = hour
                    adapter?.getItem(code)?.minute = minute
                    adapter?.getItem(code)?.repeat = timeDialog?.weekRepestValue?.toByte()!!
                    adapter?.getItem(code)?.isOpen = true
                    adapter?.notifyDataSetChanged()

                    adapter?.getItem(code)?.let { setChooseAlarm(it) }

                }

                override fun onClickRepeatClick() {
                    showWeekRepeat(alarmList.get(code).repeat)
                }
            })
        timeDialog?.create()?.show()

    }


    private fun setChooseAlarm(alarmBean: AlarmBean){
        viewModel.updateItemAlarm(BaseApplication.getInstance().bleOperate,
            alarmBean
        )
    }


    private val weekList = mutableListOf<String>()

    private var weekMap = HashMap<Int,Byte>()

    private fun showWeekRepeat(repeat : Byte){
        weekList.clear()
        weekMap.clear()

        Timber.e("----reapat="+repeat)

        weekList.add(resources.getString(R.string.sun))
        weekList.add(resources.getString(R.string.mon))
        weekList.add(resources.getString(R.string.tue))
        weekList.add(resources.getString(R.string.wed))
        weekList.add(resources.getString(R.string.thu))
        weekList.add(resources.getString(R.string.fri))
        weekList.add(resources.getString(R.string.sat))

        weekMap[0] = 1
        weekMap[1] = 2
        weekMap[2] = 4
        weekMap[3] = 8
        weekMap[4] = 16
        weekMap[5] = 32
        weekMap[6] = 64

        val repeatList = mutableListOf<Int>()
        val array = intArrayOf(1, 2, 4, 8, 16, 32, 64)

        if(repeat.toInt() == 0){
            repeatList.add(-1)
        }else{
            for (i in array.indices) {
                val v = (repeat and array[i].toByte()).toInt()
                if(v == array[i]){
                    repeatList.add(i)
                }
            }
        }

       Timber.e("----list="+Gson().toJson(repeatList))

        val weekDialog = SelectDialog.Builder(this)
            .setList(weekList)
            .setSelect(repeatList)
            .setListener { dialog, data ->
                val tempWMap = HashMap<Int,Int>()
                val stringBuilder = StringBuilder()
                dialog.dismiss()
                data.forEach {
                    if(it.key == -1){
                        tempWMap[-1] = 0
                        stringBuilder.append(it.value)
                    }else{
                        tempWMap[it.key] = 0
                        stringBuilder.append(it.value)
                    }
                }
              //  Log.e("AA", "-----周=" + data.toString())
                timeDialog?.setChooseRepeat(stringBuilder.toString())
                saveAlarm(tempWMap)
            }
            .create().show()
    }


    //保存闹钟
    private fun saveAlarm(map : HashMap<Int,Int>){

        var resultRepeat = 0
        map.forEach {
            if(it.key == -1){
                resultRepeat = 0
            }else{
                weekMap.forEach { i, i2 ->

                    if(it.key == i){
                        val value = i2
                        resultRepeat += value
                    }

                }
            }

        }
        timeDialog?.weekRepestValue = resultRepeat

    }


    var stringBuilder = java.lang.StringBuilder()
    private fun getRepeat(repeat: Byte): String? {
        val repeatStr = ""
        stringBuilder.delete(0, stringBuilder.length)
        //转bit
        val bitStr = Utils.byteToBit(repeat)
        val repeatArray = Utils.byteToBitOfArray(repeat)
        if (repeat.toInt() == 0) {
            return resources.getString(R.string.once)
        }

        //[0, 0, 0, 1, 0, 0, 0, 1] 周四，周日
        if (repeat.toInt() == 127) {  //每天
            return resources.getString(R.string.every_day)
        }
        //周末
        if ((repeat and 0xff.toByte()).toInt() == 65) {
            return resources.getString(R.string.wenkend_day)
        }
        if ((repeat and 0xff.toByte()).toInt() == 62) {  //工作日
            return resources.getString(R.string.work_day)
        }
        if (repeatArray[7].toInt() == 1) {    //周日
            stringBuilder.append(resources.getString(R.string.sun))
        }
        if (repeatArray[6].toInt() == 1) {    //周一
            stringBuilder.append(resources.getString(R.string.mon))
        }
        if (repeatArray[5].toInt()  == 1) {    //周二
            stringBuilder.append(resources.getString(R.string.tue))
        }
        if (repeatArray[4].toInt()  == 1) {    //周三
            stringBuilder.append(resources.getString(R.string.wed))
        }
        if (repeatArray[3].toInt()  == 1) {    //周四
            stringBuilder.append(resources.getString(R.string.thu))
        }
        if (repeatArray[2].toInt()  == 1) {    //周五
            stringBuilder.append(resources.getString(R.string.fri))
        }
        if (repeatArray[1].toInt()  == 1) {    //周六
            stringBuilder.append(resources.getString(R.string.sat))
        }
        return stringBuilder.toString()
    }
}