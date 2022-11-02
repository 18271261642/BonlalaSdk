package com.bonlala.fitalent.activity.history

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.RecordHistoryActivity
import com.bonlala.fitalent.db.model.SleepItem
import com.bonlala.fitalent.db.model.SleepModel
import com.bonlala.fitalent.dialog.HistorySleepDescDialog
import com.bonlala.fitalent.dialog.SleepTxtDescDialogView
import com.bonlala.fitalent.listeners.OnRecordHistoryRightListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.HistorySleepViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_history_bot_date_layout.*
import kotlinx.android.synthetic.main.fragment_history_sleep_layout.*
import timber.log.Timber

/**
 * 睡眠的fragment
 * Created by Admin
 *Date 2022/10/8
 */
class HistorySleepFragment : TitleBarFragment<RecordHistoryActivity>(),OnRecordHistoryRightListener {

    private val viewModel by viewModels<HistorySleepViewModel>()

    private var sleepModel : SleepModel ?= null

    private var dayStr : String ?= null

    //睡眠的记录
    private var recordList : List<String> ?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_history_sleep_layout
    }

    override fun initView() {

        setOnClickListener(R.id.commonHistoryLeftImg,R.id.commonHistoryRightImg,
            R.id.commonHistoryCurrentTv,R.id.historySleepThumbImg,R.id.commonHistoryCalendarImg)

        attachActivity.setOnRecordHistoryRightListener(this)
    }

    override fun initData() {

        getSleepRecord()
        showEmptyData()
        backCurrentDay()
        viewModel.onDaySleep.observe(viewLifecycleOwner){
            Timber.e("------sleept="+Gson().toJson(it))
            this.sleepModel = it
            if(it == null || it.countSleepTime == 0){
                showEmptyData()
            }else{
                showSleepData(it)
            }
        }
    }


    //获取记录
    private fun getSleepRecord(){
        viewModel.recordSleep.observe(viewLifecycleOwner){
            recordList = it
        }
        val mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac))
            return
        viewModel.getSleepRecord(mac)
    }

    //展示空的数据
    private fun showEmptyData(){
        historySleepHourTv.text ="-"
        historySleepMinuteTv.text = "-"
        detailSleepView.isCanvasStartTime = true
        detailSleepView.isNeedClick = true
        detailSleepView.sleepModel = null

    }

    //展示睡眠数据
    private fun showSleepData(sleepModel: SleepModel){

        detailSleepView.isCanvasStartTime = true
        detailSleepView.isNeedClick = true
        detailSleepView.sleepModel = sleepModel
        val countTime =  sleepModel.countSleepTime/60
        historySleepHourTv.text = countTime.toString()
        historySleepMinuteTv.text = ( sleepModel.countSleepTime % 60).toString()
    }


    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id
        when(id){
            //弹窗
            R.id.historySleepThumbImg->{
                showSleepDialog()
            }

            R.id.commonHistoryCurrentTv->{  //回到当天
                backCurrentDay()
            }
            R.id.commonHistoryLeftImg->{    //上一天
                checkSleepData(true)
            }
            R.id.commonHistoryRightImg->{   //后一天
                checkSleepData(false)
            }
            R.id.commonHistoryCalendarImg->{    //日历
                showCalendarDialog()
            }
        }
    }


    private fun showCalendarDialog(){
        showCalendarSelectDialog(recordList
        ) {
            dayStr = it
            hidCalendarDialog()
            getDbSleep()
        }
    }


    private fun getDbSleep(){
        val mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac))
            return
        viewModel.getSleepForDay(mac,dayStr.toString())

        commonHistoryDateTv.text = dayStr
        if(!BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.VISIBLE
        }else{
            commonHistoryCurrentTv.visibility = View.GONE
        }
    }


    private fun checkSleepData(date : Boolean){
        val timeLong = BikeUtils.getBeforeOrAfterDay(dayStr,date)
        dayStr = BikeUtils.getFormatDate(timeLong,"yyyy-MM-dd")
        if(BikeUtils.daySize(dayStr,BikeUtils.getCurrDate())){
            commonHistoryCurrentTv.visibility = View.GONE
            return
        }
        getDbSleep()
    }

    //回到当天
    private fun backCurrentDay(){
        dayStr = BikeUtils.getCurrDate();
        getDbSleep()
    }


    private fun showSleepDialog(){
        val sleepDialog = activity?.let { HistorySleepDescDialog(it, com.bonlala.base.R.style.BaseDialogTheme) }
        sleepDialog?.show()
        (if(sleepModel == null) SleepModel() else sleepModel)?.let { sleepDialog?.setSleepModel(it) }
//        val windowM = sleepDialog?.window?.windowManager
//        val layoutP = sleepDialog?.window?.attributes
//        layoutP?.gravity = Gravity.CENTER
//        val width = ViewGroup.LayoutParams.MATCH_PARENT
//
//        val metrics2: DisplayMetrics = resources.displayMetrics
//        val widthW: Int = metrics2.widthPixels
//        layoutP?.width = width
//        sleepDialog?.window?.attributes = layoutP
    }



    override fun onRightImgClick() {
        showSleepDesc()
    }

    override fun onHistoryClick() {

    }

    override fun onMeasureClick() {

    }

    private fun showSleepDesc(){
        val desc = SleepTxtDescDialogView(activity, com.bonlala.base.R.style.BaseDialogTheme)
        desc.show()
        desc.setDesc(resources.getString(R.string.string_sleep_txt_desc))
//        val windowM = desc.window?.windowManager
//        val layoutP = desc.window?.attributes
//        layoutP?.gravity = Gravity.CENTER
//        val width = ViewGroup.LayoutParams.MATCH_PARENT
//
//        val metrics2: DisplayMetrics = resources.displayMetrics
//        val widthW: Int = metrics2.widthPixels
//
//        layoutP?.width = widthW/2
//        desc.window?.attributes = layoutP
    }
}