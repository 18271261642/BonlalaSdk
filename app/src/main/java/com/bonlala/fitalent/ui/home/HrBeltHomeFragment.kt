package com.bonlala.fitalent.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.BleConstant
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.MainActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.history.ExerciseRecordActivity
import com.bonlala.fitalent.adapter.HomeUiAdapter
import com.bonlala.fitalent.bean.HomeSourceBean
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DataRecordModel
import com.bonlala.fitalent.db.model.ExerciseModel
import com.bonlala.fitalent.dialog.HrBletDisconnDialog
import com.bonlala.fitalent.emu.*
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.listeners.OnStartOrEndListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.view.HomeDeviceStatusView
import com.bonlala.fitalent.view.HrBeltRealTimeView
import com.bonlala.fitalent.viewmodel.HrBeltViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_home_wall_real_hr_layout.*
import timber.log.Timber


/**
 * 心率带W561B设备
 * Created by Admin
 *Date 2022/12/13
 */
class HrBeltHomeFragment : TitleBarFragment<HomeActivity>(){


    private val viewModel by viewModels<HrBeltViewModel>()

    fun  getInstance() : HrBeltHomeFragment{
        return HrBeltHomeFragment()
    }


    private var hrBeltRealTimeView : HrBeltRealTimeView ?= null
    private var homeDeviceStatusView : HomeDeviceStatusView ?= null

    //实时心率的临时集合，用于计算最大最小心率
    private var realTimeHrList = mutableListOf<Int>()


    //是否开始运动了
    private var isStartSport = false
    //运动开始记录的心率数据
    private var recordExerciseHrList = mutableListOf<Int>()



    //数据源
    private var sourceList = mutableListOf<HomeSourceBean>()

    private var homeRecyclerView : RecyclerView?= null
    private var homeUiAdapter : HomeUiAdapter?= null


    private var mHandler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0x00){
               getDataForDb()
            }
        }
    }




    override fun getLayoutId(): Int {
        return R.layout.fragment_hr_belt_home_layout
    }

    override fun initView() {
        homeRecyclerView = findViewById(R.id.homeRecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        homeDeviceStatusView = findViewById(R.id.homeDeviceStatusView)
        hrBeltRealTimeView = findViewById(R.id.hrBeltRealTimeView)
        //取消动画
        homeRecyclerView?.itemAnimator = null
        homeRecyclerView?.layoutManager = linearLayoutManager
        homeUiAdapter = HomeUiAdapter(activity,sourceList)
        homeRecyclerView?.adapter = homeUiAdapter

        homeUiAdapter?.setOnItemClickListener {
            startActivity(Intent(attachActivity,ExerciseRecordActivity::class.java))
        }

        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.COMM_BROADCAST_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        activity?.registerReceiver(broadcastReceiver, intentFilter)

        homeDeviceClick()

       //开始或结束的状态
        hrBeltRealTimeView?.setOnStartEndListener(object : OnStartOrEndListener{
            //开始
            override fun startOrEndStatus(isStart: Boolean) {
                isStartSport = true
                recordExerciseHrList.clear()
                //将柱状图的数据清0
                hrBeltRealTimeView?.setClearRealHrBarChartView()
                //将运动的累计卡路里清0
                hrBeltRealTimeView?.setInitTotalKcal()
                //隐藏底部菜单
                attachActivity.setVisibilityBottomMenu(false)
                //将锻炼记录隐藏掉
                homeRecyclerView?.visibility = View.GONE
            }
            //结束
            override fun endSportStatus(startTime: Long, sportTime: Int) {
                endSport(startTime,sportTime)
            }

        })

    }



    //结束运动
    private fun endSport(startTime: Long, sportTime: Int){
        val hour = sportTime / 3600
        val minute = (sportTime - hour * 3600) / 60
        val seconds = sportTime % 60

        isStartSport = false
        //计算数据，保存到数据库
        attachActivity.setVisibilityBottomMenu(true)
        //将锻炼记录隐藏掉
        homeRecyclerView?.visibility = View.VISIBLE
        val recordModel = DataRecordModel()
        recordModel.dayStr = BikeUtils.getCurrDate()
        val status = hrBeltRealTimeView?.getCountDownStatus()
        recordModel.dataType = DbType.DB_TYPE_EXERCISE
        recordModel.deviceMac = MmkvUtils.getConnDeviceMac()
        recordModel.deviceName = MmkvUtils.getConnDeviceName()
        recordModel.saveLongTime = System.currentTimeMillis()


        //计算最大最小，平均心率
        val avgValue = recordExerciseHrList.average().toInt()

        val exerciseModel = ExerciseModel()
        val allKcal = hrBeltRealTimeView?.getSportTotalKcal()?.toInt()
        if (allKcal != null) {
            exerciseModel.kcal = allKcal
        }
        exerciseModel.userId = "user_1001"
        exerciseModel.endTime = BikeUtils.getFormatDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss")
        exerciseModel.avgHr = avgValue
        exerciseModel.dayStr = BikeUtils.getCurrDate()
        exerciseModel.hrArray = Gson().toJson(recordExerciseHrList)
        exerciseModel.startTime = BikeUtils.getFormatDate(startTime,"yyyy-MM-dd HH:mm:ss")
        exerciseModel.deviceMac = MmkvUtils.getConnDeviceMac()
        exerciseModel.sportHour = hour
        exerciseModel.sportMinute = minute
        exerciseModel.sportSecond = seconds
        //运动类型
        exerciseModel.type = W560BExerciseType.getHrBeltSportType(status)

        if (allKcal != null) {
            hrBeltRealTimeView?.showAlertIsSave(avgValue,exerciseModel.hourMinute,allKcal,exerciseModel
            ) { mHandler.sendEmptyMessageDelayed(0x00, 500) }
        }

    }


    //连接设备的点击
    private fun homeDeviceClick(){

        //设置显示的图片
        homeDeviceStatusView?.setRightImgView(DeviceType.DEVICE_561)


        homeDeviceStatusView?.setOnClickListener {

            val connMac = MmkvUtils.getConnDeviceMac()

            if(BikeUtils.isEmpty(connMac)){
                startActivity(MainActivity::class.java)
                return@setOnClickListener
            }
        }


        //连接状态不可点击
        if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED){
            return
        }

        homeDeviceStatusView?.setCanClickStatus(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED,object : HomeDeviceStatusView.OnStatusViewClick{
            override fun onStatusClick() {
                val userMac = DBManager.getBindMac()
                if(BikeUtils.isEmpty(userMac)){
                    startActivity(MainActivity::class.java)
                    return
                }
                if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.IS_SYNC_DATA){

                    return
                }

                BaseApplication.getInstance().connStatus = ConnStatus.CONNECTING
                attachActivity.autoConnDevice()
                homeDeviceStatusView?.setHomeConnStatus(ConnStatus.CONNECTING)
            }
        })

    }



    override fun initData() {

        showDeviceStatus()

        showEmptyData()
        hrBeltRealTimeView?.setClearRealHrBarChartView()
        realTimeHrList.clear()
        BaseApplication.getInstance().bleOperate.setRealTimeDataListener { ht, step, kcal, distance ->
            Timber.e("----实时心率="+ht)
            showRealHr(ht)
        }

        viewModel.todayExercise.observe(this){
            if(it == null){
                showEmptyData()
            }else{
                sourceList.get(0).dataSource = Gson().toJson(it)
                homeUiAdapter?.notifyDataSetChanged()
            }
        }
    }


    //计算实时的心率
    private fun showRealHr(ht : Int){
        if(ht<30){
            hrBeltRealTimeView?.setZeroRealChart()
            return
        }
        realTimeHrList.add(ht)
        if(isStartSport){
            recordExerciseHrList.add(ht)
        }

        //最大心率
        val maxValue = realTimeHrList.maxOf { i : Int -> i }
        //最小值
        val minValue = realTimeHrList.minOf { i : Int -> i }
        val avgValue = realTimeHrList.average().toInt()
        hrBeltRealTimeView?.setRealTimeHrValue(ht,maxValue,minValue,avgValue,isStartSport)
    }

    //展示空数据，可以作为初始化使用
    private fun showEmptyData(){
        sourceList.clear()
        //连续心率
//        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_DETAIL_HR,null))
        //运动记录
        sourceList.add(HomeSourceBean(HomeDateType.HOME_HR_WALL_SPORT_RECORD,null))
        homeUiAdapter?.notifyDataSetChanged()

        //将柱状图的数据清0
        hrBeltRealTimeView?.setClearRealHrBarChartView()
        //将运动的累计卡路里清0
        hrBeltRealTimeView?.setInitTotalKcal()
    }


    override fun onActivityResume() {
        super.onActivityResume()
        Timber.e("------onActivityResume")

    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        Timber.e("------onFragmentResume"+(activity?.isFinishing)+" "+first)
        showDeviceStatus()
    }




    //展示连接状态
    private fun showDeviceStatus(){
        //判断是否连接过
        val bindMac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(bindMac)){  //未连接过
            homeDeviceStatusView?.setIsConnRecord(false,"")
            hrBeltRealTimeView?.visibility = View.GONE
            showEmptyData()
            return
        }
        val userMac = DBManager.getBindMac()
        if(BikeUtils.isEmpty(userMac)){
            if(hrBeltRealTimeView != null){
                hrBeltRealTimeView?.visibility = View.GONE
            }
            return
        }
        if(hrBeltRealTimeView != null){
            hrBeltRealTimeView?.visibility = View.VISIBLE
        }

        getDataForDb()
        val bleName = MmkvUtils.getConnDeviceName()
        homeDeviceStatusView?.setIsConnRecord(true,bleName)
        //连接过，判断状态
        homeDeviceStatusView?.setHomeConnStatus(BaseApplication.getInstance().connStatus)

        val status = hrBeltRealTimeView?.getCountDownStatus()
        //如果未连接就不显示实时心率
        if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || status != CountDownStatus.DEFAULT_STATUS){
            hrBeltRealTimeView?.visibility = View.VISIBLE
        }else{
            hrBeltRealTimeView?.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        getDataForDb()
    }

    private fun getDataForDb(){
        val mac = DBManager.getBindMac()
        if(BikeUtils.isEmpty(mac)){
            return
        }
        viewModel.getTodayExerciseData(mac,attachActivity)
    }


    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(broadcastReceiver)
    }



    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            Timber.e("------心率带--action="+action)
            //连接状态
            if (action == BleConstant.BLE_CONNECTED_ACTION || action == BleConstant.BLE_DIS_CONNECT_ACTION || action == BleConstant.BLE_SCAN_COMPLETE_ACTION){


                val isConn = BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus ==ConnStatus.IS_SYNC_DATA
                if(!isConn ){
                    showReconnDialog()
                }

                showDeviceStatus()
            }
        }

    }

    var reconnDialog : HrBletDisconnDialog ?= null

    //展示重连的弹窗
    private fun showReconnDialog(){
        if(reconnDialog == null){
            reconnDialog = HrBletDisconnDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        }
        reconnDialog!!.show()
        reconnDialog!!.setOnSportSaveClickListener(object : OnItemClickListener{
            override fun onIteClick(position: Int) {

                if(position == 0x00){   //结束运动
                    reconnDialog!!.dismiss()
                }

                if(position == 0x01){   //重连

                }
            }
        })
    }


    private var forwardCountTime = 0

    private var handlers: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0x00){
                forwardCountTime++
                hrBeltTimerTv.text = BikeUtils.formatSecond(forwardCountTime)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Timber.e("----onDestroyView----")
    }
}