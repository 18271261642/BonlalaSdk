package com.bonlala.fitalent.view

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.bean.HrBeltGroupBean
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.ExerciseModel
import com.bonlala.fitalent.dialog.HeightSelectDialog
import com.bonlala.fitalent.dialog.HrBeltModelSelectView
import com.bonlala.fitalent.dialog.HrBeltSaveDialogView
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.CountDownStatus
import com.bonlala.fitalent.emu.DbType
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.listeners.OnStartOrEndListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.HeartRateConvertUtils
import com.google.gson.Gson
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.item_home_wall_real_hr_layout.*
import kotlinx.android.synthetic.main.item_home_wall_real_hr_layout.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat

/**
 * Created by Admin
 *Date 2022/12/14
 */
class HrBeltRealTimeView : LinearLayout {

    private val decimalFormat = DecimalFormat("#.##")

    private var onStartOrEndListener : OnStartOrEndListener ?= null

    fun setOnStartEndListener(onListener : OnStartOrEndListener){
        this.onStartOrEndListener = onListener
    }


    //是哪种状态
    private var countDownStatus = CountDownStatus.DEFAULT_STATUS

    //开始的按钮
    private var hrBeltStartTv : ShapeTextView ?= null
    //显示的时间HH:mm:ss格式
    private var hrBeltTimerTv : TextView ?= null
    //运动中的布局
    private var hrBeltContentLayout : LinearLayout ?= null
    //运动中的卡路里
    private var hrBeltExerciseKcal : TextView ?= null
    //长按停止的按钮
    private var hrBeltPressView : PausePressView ? = null
    //进度条
    private var hrBeltGroupView : HrBeltGroupView ?= null


    //倒计时
    private var countDownTimer : CountDownTimer ?= null

    //分组的倒计时
    private var groupCountDownTimer : CountDownTimer ?= null
    //分组的集合
    private var groupTimeList = mutableListOf<HrBeltGroupBean>()

    //开始时间，long类型，精确到毫秒
    private var sportStartTime = 0L
    //运动的总时长，秒
    private var sportAllTime = 0
    //运动总的卡路里
    private  var totalSportKcal = 0.0

    //正向计时器
    private var forwardCountTime = 0

    private var handlers: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0x00){
                forwardCountTime++
                hrBeltTimerTv?.text = BikeUtils.formatSecond(forwardCountTime)
            }
        }
    }


    constructor(context: Context) : super (context){
        initViews(context)
    }

    constructor(context: Context,attributeSet: AttributeSet) : super (context,attributeSet){
        initViews(context)
    }

    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr : Int) : super (context,attributeSet,defStyleAttr){
        initViews(context)
    }



    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_home_wall_real_hr_layout,this,true)
        hrBeltStartTv = view.findViewById(R.id.hrBeltStartTv)
        hrBeltTimerTv = view.findViewById(R.id.hrBeltTimerTv)
        hrBeltContentLayout = view.findViewById(R.id.hrBeltContentLayout)
        hrBeltGroupView = view.findViewById(R.id.hrBeltGroupView)
        hrBeltPressView = view.findViewById(R.id.hrBeltPressView)
        hrBeltExerciseKcal = view.findViewById(R.id.hrBeltExerciseKcal)


        initViews()

    }


    private fun initViews(){
        //开始按钮，点击后弹窗选择模式的弹窗
        hrBeltStartTv?.setOnClickListener {
            //未连接不允许点击
            if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED){
                ToastUtils.show(resources.getString(R.string.string_not_connect))
                return@setOnClickListener
            }
            showModelDialog()
        }

        //长按停止的按钮
        hrBeltPressView?.setOnCountDownStateChangeListener(object : PausePressView.OnCountDownStateChangeListener{
            override fun onCountDownEnd() {
               cancelGroupCountDown()
                stopCountDown()
                if(countDownStatus == CountDownStatus.FORWARD_STATUS){
                    completeSport()
                }

            }

            override fun onCountDownCancel() {

            }

        })
    }


    //结束，提示是否保存，
    private fun completeSport(){

        handlers.removeCallbacks(runnable)
        //运动的时长
        if(countDownStatus == CountDownStatus.FORWARD_STATUS){
            sportAllTime = forwardCountTime
        }else{
            sportAllTime = hrBeltGroupView?.getCurrentSchedule()!!
        }

        //结束的回调
        onStartOrEndListener?.endSportStatus(sportStartTime, sportAllTime)


        forwardCountTime = 0
        hrBeltContentLayout?.visibility = View.GONE
        hrBeltStartTv?.visibility = View.VISIBLE

    }



    //清除柱状图数据
    fun setClearRealHrBarChartView(){
        homeRealHtView.clearData()
    }

    //运动开始时将累计的卡路里清0
    fun setInitTotalKcal(){
      totalSportKcal = 0.0
    }



    /**
     * 设置显示实时心率和最大值最小值
     */
    fun setRealTimeHrValue(heartValue : Int,maxValue : Int,minValue : Int,avgValue : Int,isSport : Boolean){
        //最大心率
        val maxHr = HeartRateConvertUtils.getUserMaxHt()
        //心率强度百分比
        val hrPercent = HeartRateConvertUtils.hearRate2Percent(heartValue, maxHr)

        //心率点数，根据点数设置背景和颜色
        val point = HeartRateConvertUtils.hearRate2Point(heartValue, hrPercent)
        val color = HeartRateConvertUtils.getColorByPoint(context, point)

        //心率强度百分比，转整
        itemRealHrPercentageTv.text = hrPercent.toInt().toString()+"%"
        itemRealHrPercentageTv.setTextColor(color)

        //背景颜色图片
        hrBeltBgLayout.background = HeartRateConvertUtils.setHrBeltDrawable(context, point)
        //柱状图
        homeRealHtView.addData(heartValue,color,false)
        itemHomeRealHrValue.text = heartValue.toString()
        itemHomeRealHrValue.setTextColor(color)


        hrBeltMaxHrTv.text = getTargetType(maxValue.toString(),"bpm")
        hrBeltMinHrTv.text = getTargetType(minValue.toString(),"bpm")
        hrBeltAvgHrTv.text = getTargetType(avgValue.toString(),"bpm")


        if(isSport){
            val userAgeAndWeight = HeartRateConvertUtils.getUserAgeAndWeight()
            val kal = HeartRateConvertUtils.hearRate2CaloriForMan(heartValue,
                userAgeAndWeight[0], userAgeAndWeight.get(1).toFloat()
            )

            totalSportKcal+=kal

            Timber.e("-----totalKcal="+totalSportKcal+" "+kal)

            setSportKcal(totalSportKcal.toFloat())
        }
    }




    //选择模式弹窗
    private fun showModelDialog(){
        currentGroupTv.text = ""

        val modelDialog = HrBeltModelSelectView(context, com.bonlala.base.R.style.BaseDialogTheme)
        modelDialog.show()
        modelDialog.setOnBeltModelSelectListener { position ->
            modelDialog.dismiss()
            //开始之前先把横向的进度条清理一下
            hrBeltGroupView?.setInitCurrent()
            if (position == 0x00) {   //正向计时
                hrBeltContentLayout?.visibility = View.VISIBLE
                hrBeltStartTv?.visibility = View.GONE
                modelDialog.dismiss()
                showForwardTime()
            }

            if (position == 0x01) {   //倒计时，需要设置总时间
                modelDialog.dismiss()

                showTimeOrGroupDialog(false)
            }
            if (position == 0x02) {   //分组计时
                showTimeOrGroupDialog(true)
            }

        }
    }


    /**
     * 选择时间或者分组的弹窗 是否是分组
     * 倒计时选择时间：1~150分钟，默认20分钟
     * 分钟时间选择：1~30分钟 默认3分钟
     */
    private fun showTimeOrGroupDialog(isGroup : Boolean){
        val sourceList = mutableListOf<String>()
        if(isGroup){
            for(i in 1..30){
                sourceList.add(i.toString())
            }
        }else{
            for(i in 1..150){
                sourceList.add(i.toString())
            }
        }

        val dialog = HeightSelectDialog.Builder(context,sourceList)
            .setUnitShow(true,resources.getString(R.string.string_minute))
            .setDefaultSelect(if(isGroup) 2 else 19)
            .setTitleTx(resources.getString(R.string.string_select_time))
            .setSignalSelectListener {
                Timber.e("-------默认选="+it)
                if(isGroup){
                    showGroupRestTime(it.toInt() *60)
                }else{
                    countDownStatus = CountDownStatus.COUNTDOWN_STATUS
                    startCountDown(it.toInt() * 60)
                }

            }.show()
    }


    /**
     * 分钟，选择休息时间
     * 10~120秒 间隔10秒 默认30秒
     * sportTime 已经选择的运动时间，已转化成秒
     */
    private fun showGroupRestTime(sportTime : Int){
        val restList = mutableListOf<String>()
        for(i in 1 .. 12){
            val time = i * 10
            restList.add(time.toString())
        }

        val dialog = HeightSelectDialog.Builder(context,restList)
            .setUnitShow(true,resources.getString(R.string.string_second))
            .setDefaultSelect(2)
            .setConfirmTxt(resources.getString(R.string.string_next))
            .setTitleTx(resources.getString(R.string.string_select_group_rest))
            .setSignalSelectListener {
                Timber.e("------降额="+it)
                //中途休息的时间
                val restTime = it.toInt()

                showGroupGroupSelect(sportTime,restTime)

            }.show()

    }


    /**
     * 分组 选择组数
     * 1~5组 间隔1组，默认3组
     */
    private fun showGroupGroupSelect(sportTime: Int,restTime : Int){
        groupTimeList.clear()
        val groupList = mutableListOf<String>()
        for(i in 1..5){
            groupList.add(i.toString())
        }

        val dialog = HeightSelectDialog.Builder(context,groupList)
            .setUnitShow(true,resources.getString(R.string.string_group))
            .setDefaultSelect(2)
            .setConfirmTxt(resources.getString(R.string.string_start))
            .setTitleTx(resources.getString(R.string.string_select_groups))
            .setSignalSelectListener {

                //几组
                val groups = it.toInt()
                dealWidthGroupTimeData(sportTime,restTime,groups)

            }.show()


    }




    //处理分组的倒计时
    private fun dealWidthGroupTimeData(sportTime: Int,restTime : Int,groupNumbers : Int){

        //所有的时间，运动时间+中途休息时间
        var maxTime = 0

        val groupList = mutableListOf<HrBeltGroupBean>()
        for(i in 0 until groupNumbers){

            //运动的开始时间
            val startTime = 0
            //运动结束时间
            val endTime = sportTime
            val hrBean = HrBeltGroupBean(1,maxTime,endTime+maxTime)
            maxTime+=endTime
            groupList.add(hrBean)
            if(i<groupNumbers-1){
                val hrRestBean = HrBeltGroupBean(0,maxTime,restTime+maxTime)
                maxTime+=restTime
                groupList.add(hrRestBean)
            }

        }

        groupTimeList.addAll(groupList)

        hrBeltContentLayout?.visibility = View.VISIBLE
        hrBeltStartTv?.visibility = View.GONE

        Timber.e("--------分钟运动="+maxTime+" "+Gson().toJson(groupList))

        countDownStatus = CountDownStatus.COUNTDOWN_STATUS

        hrBeltGroupView?.setBackgroundSource(groupList,maxTime)
//        hrBeltGroupView?.setInitCurrent()

        startGroupCountDown(maxTime)
    }


    //开始正向计时
    private fun showForwardTime(){
        sportStartTime = System.currentTimeMillis()
        countDownStatus = CountDownStatus.FORWARD_STATUS
        //开始的回调
        onStartOrEndListener?.startOrEndStatus(true)
        handlers.postDelayed(runnable,0)
    }


    var runnable: Runnable = object : Runnable {
        override fun run() {
            handlers.sendEmptyMessage(0x00)
            handlers.postDelayed(this, 1000)
        }
    }


    var timeLong = 0
    //开始倒计时,转换成秒
    private fun startCountDown(maxTime : Int){
        hrBeltContentLayout?.visibility = View.VISIBLE
        hrBeltStartTv?.visibility = View.GONE
        sportStartTime = System.currentTimeMillis()

        val backList = mutableListOf<HrBeltGroupBean>()
        backList.add(HrBeltGroupBean(0,0,maxTime))
        hrBeltGroupView?.setBackgroundSource(backList,maxTime)
        //开始的回调
        onStartOrEndListener?.startOrEndStatus(true)

        countDownTimer = object  : CountDownTimer(maxTime * 1000L + 50 ,1000){
            override fun onTick(p0: Long) {
                timeLong = (p0/1000).toInt()
                Timber.e("-----倒计时="+timeLong)
                hrBeltTimerTv?.text = BikeUtils.formatSecond(timeLong)

                hrBeltGroupView?.setCurrentSchedule(maxTime-timeLong)
            }

            override fun onFinish() {
                Timber.e("------结束了=")
                completeSport()
            }
        }

        countDownTimer?.start()
    }


    //结束计时
    private fun stopCountDown(){
        countDownTimer?.onFinish()
        countDownTimer?.cancel()
    }


    /**
     * 分组的倒计时，以总的时间为计时长度，分组判断
     * 秒
     */

    var groupLong = 0

    private fun startGroupCountDown(maxTime: Int){
        countDownStatus = CountDownStatus.GROUP_STATUS
        groupCountDownTimer?.cancel()
        sportStartTime = System.currentTimeMillis()
        //开始的回调
        onStartOrEndListener?.startOrEndStatus(true)

        groupCountDownTimer = object : CountDownTimer(maxTime * 1000L +50,1000){
            override fun onTick(p0: Long) {
                groupLong = (p0 / 1000).toInt()

                //正向的
                val forwardTime = maxTime - groupLong
                hrBeltGroupView?.setCurrentSchedule(forwardTime)

                Timber.e("-----分组计时="+groupLong+" "+forwardTime+" "+p0)

                groupTimeList.forEachIndexed { index, hrBeltGroupBean ->

                    //类别
                    val type = hrBeltGroupBean.type

                    //开始时间
                    val startT = hrBeltGroupBean.startTime
                    val endT = hrBeltGroupBean.endTime


                    //判断在哪个倒计时区间内
                    if(forwardTime in (startT + 1)..endT){

                        currentGroupTv.text = if(type == 0) resources.getString(R.string.string_resting) else (resources.getString(R.string.string_current_group)+(index/2+1) +"/"+((groupTimeList.size+1)/2))

                        hrBeltTimerTv?.text = BikeUtils.formatSecond(endT-forwardTime)

                    }


                    //结束
                    if(p0<=1000){
//                        groupCountDownTimer?.onFinish()
                       // cancelGroupCountDown()
                        Timber.e("------结束了======"+p0)

                    }
                }

            }

            override fun onFinish() {
                Timber.e("-------onFinish=-")
                completeSport()
            }

        }
        groupCountDownTimer?.start()

    }


    //关闭分组的倒计时
    private fun cancelGroupCountDown(){
        groupCountDownTimer?.onFinish()
        groupCountDownTimer?.cancel()
    }


    /**
     * 是否保存的弹窗
     * 设置运动显示的数据
     */
    fun showAlertIsSave(avg : Int,max : Int,min : Int,exercise : ExerciseModel,onItemClickListener: OnItemClickListener){
        val isSaveDialog = HrBeltSaveDialogView(context, com.bonlala.base.R.style.BaseDialogTheme)
        isSaveDialog.show()
        isSaveDialog.setHeartValue(avg,max,min)
        isSaveDialog.setOnSportSaveClickListener {
            if(it == 0x01){ //保存
                DBManager.dbManager.saveExerciseData(exercise.userId,exercise.deviceMac,exercise.startTime,exercise)
                GlobalScope.launch {
                    delay(500)
                    DBManager.dbManager.saveDateTypeDay(exercise.userId,exercise.deviceMac,BikeUtils.getCurrDate(),DbType.DB_TYPE_EXERCISE)
                }

                isSaveDialog.dismiss()
                onItemClickListener.onIteClick(0x00)
            }
        }
    }


    /**
     * 设置大小
     * @param value 值
     * @param unitType 单位 eg: km ,kcal ..
     * @return eg:100 km
     */
    private fun getTargetType(value: String, unitType: String): SpannableString? {
        var distance = value
        distance = "$distance $unitType"
        val spannableString = SpannableString(distance)
        spannableString.setSpan(
            AbsoluteSizeSpan(14, true),
            distance.length - unitType.length,
            distance.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#ACACAC")),
            distance.length - unitType.length,
            distance.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }



    //获取状态
    fun getCountDownStatus(): CountDownStatus{
        return countDownStatus
    }

    //获取运动累计的卡路里
    fun getSportTotalKcal():Float{
        return totalSportKcal.toFloat()
    }



    //设置显示的卡路里
    private fun setSportKcal(kcal : Float){
        hrBeltExerciseKcal?.text = decimalFormat.format(kcal)
    }

}