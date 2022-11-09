package com.bonlala.fitalent.activity.history

import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.viewModels
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.RecordHistoryActivity
import com.bonlala.fitalent.bean.ChartHrBean
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.dialog.*
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.MeasureType
import com.bonlala.fitalent.listeners.OnRecordHistoryRightListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.view.StepChartViewUtils
import com.bonlala.fitalent.viewmodel.HistoryHeartViewModel
import com.github.mikephil.charting.charts.LineChart
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.common_history_bot_date_layout.*
import kotlinx.android.synthetic.main.fragment_history_heart_layout.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * 心率详细页面
 * Created by Admin
 *Date 2022/10/10
 */
class HistoryHeartFragment : TitleBarFragment<RecordHistoryActivity>(),OnRecordHistoryRightListener{

    private var detailHeartView : LineChart ?= null


    private val viewModel by viewModels<HistoryHeartViewModel>()

    private var chartViewUtils : StepChartViewUtils ?= null

    private var dayStr : String ?= null

    //处理心率对象的集合
    private val tempHrList = mutableListOf<ChartHrBean>()

    //心率的记录
    private val hrRecordList = mutableListOf<String>()





    override fun getLayoutId(): Int {
      return R.layout.fragment_history_heart_layout
    }

    override fun initView() {
        detailHeartView = findViewById(R.id.detailHeartView)

        setOnClickListener(R.id.commonHistoryRightImg,R.id.commonHistoryLeftImg,
            R.id.commonHistoryCurrentTv,R.id.historySleepThumbImg,R.id.commonHistoryCalendarImg)
        attachActivity.setOnRecordHistoryRightListener(this)

    }

    override fun initData() {
        dayStr = BikeUtils.getCurrDate()

        showResult()

        val mac = DBManager.getBindMac()
        if(BikeUtils.isEmpty(mac)){
            showEmptyData()
            return
        }
        chartViewUtils = StepChartViewUtils(detailHeartView)
        viewModel.getAllHrRecord(mac)

        getDayHeart("innitData")
    }


    private var analysisHeartList = mutableListOf<Int>()



    private fun showResult() {

        viewModel.hrRecordList.observe(viewLifecycleOwner){
            hrRecordList.clear()
            hrRecordList.addAll(it)
        }


        analysisHeartList.clear()
        val xList = mutableListOf<String>()
        xList.add("00:00")
        xList.add("03:00")
        xList.add("06:00")
        xList.add("09:00")
        xList.add("12:00")
        xList.add("15:00")
        xList.add("18:00")
        xList.add("21:00")
        xList.add("23:59")


        viewModel.oneDayHeart.observe(viewLifecycleOwner) { it ->
           // Timber.e("-----心率=" + Gson().toJson(it)+"\n"+Gson().toJson(it.heartList))

            analysisHeartList.clear()
            if (it == null) {
                analysisHeartList.clear()
                showAvgHeart()
                detailHeartView?.visibility = View.VISIBLE
                sleepNoDataTv.visibility = View.GONE
                chartViewUtils!!.setChartData(activity, tempHrList, ArrayList<String>(),true)
                showEmptyData()
                return@observe
            }
            tempHrList.clear()
            it.heartList.forEachIndexed { index, i ->
                if (i != 0) {
                    tempHrList.add(ChartHrBean(index + 1, if(i==254)0 else i))
                    analysisHeartList.add(i)
                }
            }

            detailHeartView?.visibility = View.VISIBLE
            sleepNoDataTv.visibility = View.GONE
            // historyAvgTv.text =
            chartViewUtils!!.setChartData(activity, tempHrList, xList,true)
            showAvgHeart()

        }

    }


     private fun getDayHeart(tag : String){
        Timber.e("----getDayHr="+tag)

        val mac = DBManager.getBindMac()
            if(BikeUtils.isEmpty(mac)){
                showEmptyData()
                return
            }


        viewModel.queryOneDayHeart(mac,dayStr.toString())

         commonHistoryDateTv.text = if(BaseApplication.getInstance().isChinese) dayStr else BikeUtils.getFormatEnglishDate(dayStr)
         //判断回到当前是否显示
         commonHistoryCurrentTv.visibility =
             if (!dayStr.equals(BikeUtils.getCurrDate())) View.VISIBLE else View.GONE

     }


    //展示无数据
    private fun showEmptyData(){
        detailHeartView?.visibility = View.GONE
        sleepNoDataTv.visibility = View.VISIBLE
        historyAvgTv.text = "--"
        historyMaxHrTv.text = "--"
        historyMinHrTv.text = "--"
    }


    //展示平均、最大、最小心率
    private fun showAvgHeart(){
        Timber.e("----analys="+Gson().toJson(analysisHeartList))
        if(analysisHeartList.isEmpty()){
            historyAvgTv.text = "--"
            historyMaxHrTv.text = "--"
            historyMinHrTv.text = "--"
            return
        }


        Timber.e("------最大值="+(analysisHeartList.maxOf { it })+" "+(analysisHeartList.minOf { it }))

        //最大心率
        historyMaxHrTv.text = Collections.max(analysisHeartList).toString()
        historyMinHrTv.text = Collections.min(analysisHeartList).toString()

        var countHt = 0
        analysisHeartList.forEach {
            countHt+=it
        }
        historyAvgTv.text = (countHt / analysisHeartList.size).toString()

    }

    override fun onRightImgClick() {
        showDescDialog()
    }

    override fun onHistoryClick() {
        showSingleHrHistoryData()
    }

    override fun onMeasureClick() {
        if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED){
            ToastUtils.show(resources.getString(R.string.string_not_connect))
            return
        }
        showMeasureDialog()
    }



    private fun showMeasureDialog(){
        val measureDialog = MeasureDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        measureDialog.show()
        measureDialog.setCancelable(false)
        measureDialog.startToMeasure(MeasureType.HEART)
        measureDialog.setOnMeasureCancelListener {


        }
    }


    //描述
    private fun showDescDialog(){
        val desc = SleepTxtDescDialogView(activity, com.bonlala.base.R.style.BaseDialogTheme)
        desc.show()
        desc.setDesc(resources.getString(R.string.string_hr_txt_desc))
    }


    //展示历史
    private fun showSingleHrHistoryData(){
        val hrDialog = HistoryHeartDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        hrDialog.show()
        hrDialog.setCancelable(false)

    }


    //详情
    private fun showHrDetailDialog(){
        val hrDesc = HistoryHrDescDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        hrDesc.show()
        hrDesc.setHrStrongData(analysisHeartList)
        val layoutP = hrDesc.window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val height: Int = metrics2.heightPixels
        layoutP?.height = height
        hrDesc.window?.attributes = layoutP
    }


    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id
        when(id){
            //前一天
            R.id.commonHistoryLeftImg->{
                selectDate(true)
            }
            //后一天
            R.id.commonHistoryRightImg->{
                selectDate(false)
            }
            //回到当前
            R.id.commonHistoryCurrentTv->{
                backCurrentData()
            }

            //缩略图
            R.id.historySleepThumbImg->{
                showHrDetailDialog()
            }

            //日历
            R.id.commonHistoryCalendarImg->{
                showCalendar()
            }
        }

    }

    //显示日历
    private fun showCalendar(){
        showCalendarSelectDialog(hrRecordList
        ) { day ->
            dayStr = day
            getDayHeart("dd")
            hidCalendarDialog()
        }
    }



    private fun selectDate(isPreview : Boolean){
        val timeLong = BikeUtils.getBeforeOrAfterDay(dayStr,isPreview)
        dayStr = BikeUtils.getFormatDate(timeLong,"yyyy-MM-dd")

        if(timeLong>System.currentTimeMillis()){
            return
        }
        getDayHeart("selectDate"+isPreview)
    }

    private fun backCurrentData(){
        dayStr = BikeUtils.getCurrDate()
        getDayHeart("current")

    }
}