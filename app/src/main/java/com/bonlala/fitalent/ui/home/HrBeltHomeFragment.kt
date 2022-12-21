package com.bonlala.fitalent.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
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
import com.bonlala.fitalent.dialog.HeightSelectDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.DbType
import com.bonlala.fitalent.emu.HomeDateType
import com.bonlala.fitalent.emu.W560BExerciseType
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.listeners.OnStartOrEndListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.view.HomeDeviceStatusView
import com.bonlala.fitalent.viewmodel.HrBeltViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.homeDeviceStatusView
import kotlinx.android.synthetic.main.fragment_hr_belt_home_layout.*
import kotlinx.android.synthetic.main.item_home_wall_real_hr_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.min

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
        intentFilter.addAction(BleConstant.BLE_SYNC_COMPLETE_SET_ACTION)
        intentFilter.addAction(BleConstant.COMM_BROADCAST_ACTION)
        intentFilter.addAction(BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        activity?.registerReceiver(broadcastReceiver, intentFilter)

        homeDeviceClick()

       //开始或结束的状态
        hrBeltRealTimeView.setOnStartEndListener(object : OnStartOrEndListener{
            //开始
            override fun startOrEndStatus(isStart: Boolean) {
                isStartSport = true
                recordExerciseHrList.clear()
                //将柱状图的数据清0
                hrBeltRealTimeView.setClearRealHrBarChartView()
                //将运动的累计卡路里清0
                hrBeltRealTimeView.setInitTotalKcal()
                //隐藏底部菜单
                attachActivity.setVisibilityBottomMenu(false)
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
        val recordModel = DataRecordModel()
        recordModel.dayStr = BikeUtils.getCurrDate()
        val status = hrBeltRealTimeView.getCountDownStatus()
        recordModel.dataType = DbType.DB_TYPE_EXERCISE
        recordModel.deviceMac = MmkvUtils.getConnDeviceMac()
        recordModel.deviceName = MmkvUtils.getConnDeviceName()
        recordModel.saveLongTime = System.currentTimeMillis()


        //计算最大最小，平均心率
        //最大心率
        val maxValue = recordExerciseHrList.maxOf { i : Int -> i }
        //最小值
        val minValue = recordExerciseHrList.minOf { i : Int -> i }
        val avgValue = recordExerciseHrList.average().toInt()


        val exerciseModel = ExerciseModel()
        exerciseModel.kcal = hrBeltRealTimeView.getSportTotalKcal().toInt()
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


        hrBeltRealTimeView.showAlertIsSave(avgValue,maxValue,minValue,exerciseModel
        ) { mHandler.sendEmptyMessageDelayed(0x00, 500) }


    }


    //连接设备的点击
    private fun homeDeviceClick(){
        val userMac = DBManager.getBindMac()
        val connMac = MmkvUtils.getConnDeviceMac()

        homeDeviceStatusView.setOnClickListener {
            if(BikeUtils.isEmpty(connMac)){
                startActivity(MainActivity::class.java)
                return@setOnClickListener
            }
        }


        //连接状态不可点击
        if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED){
            return
        }

        homeDeviceStatusView.setCanClickStatus(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED,object : HomeDeviceStatusView.OnStatusViewClick{
            override fun onStatusClick() {

                if(BikeUtils.isEmpty(userMac)){
                    startActivity(MainActivity::class.java)
                    return
                }
                if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.IS_SYNC_DATA){

                    return
                }

                BaseApplication.getInstance().connStatus = ConnStatus.CONNECTING
                attachActivity.autoConnDevice()
                homeDeviceStatusView.setHomeConnStatus(ConnStatus.CONNECTING)
            }
        })

    }



    override fun initData() {

        showDeviceStatus()

        showEmptyData()
        hrBeltRealTimeView.setClearRealHrBarChartView()
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
        hrBeltRealTimeView.setRealTimeHrValue(ht,maxValue,minValue,avgValue,isStartSport)
    }

    //展示空数据，可以作为初始化使用
    private fun showEmptyData(){
        sourceList.clear()
        //连续心率
//        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_DETAIL_HR,null))
        //运动记录
        sourceList.add(HomeSourceBean(HomeDateType.HOME_HR_WALL_SPORT_RECORD,null))
        homeUiAdapter?.notifyDataSetChanged()
    }



    //展示连接状态
    private fun showDeviceStatus(){
        //判断是否连接过
        val bindMac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(bindMac)){  //未连接过
            homeDeviceStatusView.setIsConnRecord(false,"")
            showEmptyData()
            return
        }
        val userMac = DBManager.getBindMac()
        if(BikeUtils.isEmpty(userMac)){

            return
        }
        getDataForDb()
        val bleName = MmkvUtils.getConnDeviceName()
        homeDeviceStatusView.setIsConnRecord(true,bleName)
        //连接过，判断状态
        homeDeviceStatusView.setHomeConnStatus(BaseApplication.getInstance().connStatus)

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
            var action = p1?.action
            Timber.e("--------action="+action)
            //连接状态
            if (action == BleConstant.BLE_CONNECTED_ACTION || action == BleConstant.BLE_DIS_CONNECT_ACTION || action == BleConstant.BLE_SCAN_COMPLETE_ACTION){
                showDeviceStatus()
            }

            if(action == BleConstant.COMM_BROADCAST_ACTION){
                val value = p1?.getIntArrayExtra(BleConstant.COMM_BROADCAST_KEY) as IntArray
                Timber.e("------测量完查询="+value[0])
                if(value[0] == BleConstant.MEASURE_COMPLETE_VALUE){
                    //getDataForDb()
                }
            }

            if(action == BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION){

                homeDeviceStatusView.setHomeConnStatus(BaseApplication.getInstance().connStatus)

              //  getDataForDb()
            }
        }

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


    //开始正向计时
    private fun showForwardTime(){
        handlers.postDelayed(runnable,0)
    }


    var runnable: Runnable = object : Runnable {
        override fun run() {
            handlers.sendEmptyMessage(0x00)
            handlers.postDelayed(this, 1000)
        }
    }




    //选择时间或者分组的弹窗
    private fun showTimeOrGroupDialog(){
        var sourceList = mutableListOf<String>()
        for(i in 1..150){
            sourceList.add(i.toString())
        }


        val dialog = HeightSelectDialog.Builder(attachActivity,sourceList)
            .setUnitShow(true,resources.getString(R.string.string_minute))
            .setDefaultSelect(19)
            .setTitleTx(resources.getString(R.string.string_select_time))
            .setSignalSelectListener {

            }.show()
    }

}