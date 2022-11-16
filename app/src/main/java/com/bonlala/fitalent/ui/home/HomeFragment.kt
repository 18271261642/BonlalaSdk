package com.bonlala.fitalent.ui.home


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.BleConstant
import com.blala.blalable.BleOperateManager
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.MainActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.RecordHistoryActivity
import com.bonlala.fitalent.activity.history.ExerciseRecordActivity
import com.bonlala.fitalent.activity.history.TestA
import com.bonlala.fitalent.adapter.HomeUiAdapter
import com.bonlala.fitalent.bean.HomeHeartBean
import com.bonlala.fitalent.bean.HomeRealtimeBean
import com.bonlala.fitalent.bean.HomeSourceBean
import com.bonlala.fitalent.ble.DataOperateManager
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.SingleBpModel
import com.bonlala.fitalent.db.model.SingleSpo2Model
import com.bonlala.fitalent.db.model.SumSportModel
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.HomeDateType
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.GsonUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.view.HomeDeviceStatusView
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber

/**
 * 首页面
 */
class HomeFragment : TitleBarFragment<HomeActivity>() , OnRefreshListener {


    val viewModel by viewModels<HomeViewModel>()
    //数据源
    private var sourceList = mutableListOf<HomeSourceBean>()

    private var homeRecyclerView : RecyclerView ?= null
    private var homeUiAdapter : HomeUiAdapter ?= null

    private val gson = Gson()

    fun newInstance(): HomeFragment {
        return HomeFragment()
    }


    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        homeRecyclerView = findViewById(R.id.homeRecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val gridLayoutManager = GridLayoutManager(activity,2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
              if(position == 0 || position == 1 || position == 2)
                  return 2
                else
                    return 1
            }

        }


        homeRefreshLayout.setOnRefreshListener(this)

        homeRecyclerView?.itemAnimator = null
        homeRecyclerView?.layoutManager = gridLayoutManager
        homeUiAdapter = HomeUiAdapter(activity,sourceList)
        homeRecyclerView?.adapter = homeUiAdapter
        homeUiAdapter?.setOnItemClickListener {

            //判断是否用户绑定Mac了，用户绑定了Mac可以点击
            val bindMac = DBManager.getBindMac()

            if(BikeUtils.isEmpty(bindMac)){

                ToastUtils.show(resources.getString(R.string.string_not_connect))
                return@setOnItemClickListener
            }

            if(it == HomeDateType.HOME_TYPE_REAL_HR)
                return@setOnItemClickListener

            //打开实时心率
            if(it == -2){
                viewModel.setRealHeartStatus(BleOperateManager.getInstance(),true)
                return@setOnItemClickListener
            }

            //关闭实时心率
            if(it == -1){
                viewModel.setRealHeartStatus(BleOperateManager.getInstance(),false)
                sourceList[0].dataSource = gson.toJson(HomeRealtimeBean(false,0))
                homeUiAdapter?.notifyItemChanged(0)
                return@setOnItemClickListener
            }

            val intent = Intent(attachActivity,if(it == HomeDateType.HOME_TYPE_SPORT_RECORD) ExerciseRecordActivity::class.java else RecordHistoryActivity::class.java)
            intent.putExtra("home_type", it)
            startActivity(intent)
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SYNC_COMPLETE_SET_ACTION)
        intentFilter.addAction(BleConstant.COMM_BROADCAST_ACTION)
        intentFilter.addAction(BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        activity?.registerReceiver(broadcastReceiver, intentFilter)

        homeDeviceClick()

    }


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

        sourceList.clear()
        //实时心率
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_REAL_HR,null))
        //计步
        val sumStepModel = SumSportModel()
        sumStepModel.sumStep = 6200
        sumStepModel.sumKcal = 1200
        sumStepModel.sumDistance = 5
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_STEP,null))

        //连续心率
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_DETAIL_HR,null))

        //组装睡眠数据
        val dataB1 = HomeSourceBean(HomeDateType.HOME_TYPE_SLEEP,null)
        sourceList.add(dataB1)
        //血氧
        val singleSpo2Model = SingleSpo2Model(System.currentTimeMillis(),0)
        val dataB2 = HomeSourceBean(HomeDateType.HOME_TYPE_SPO2,Gson().toJson(singleSpo2Model))
        sourceList.add(dataB2)
        val singleBpModel = SingleBpModel(System.currentTimeMillis(),0,0)
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_BP,Gson().toJson(singleBpModel)))
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_SPORT_RECORD,null))

        homeUiAdapter?.notifyDataSetChanged()

        //实时数据
        BleOperateManager.getInstance().setRealTimeDataListener { ht, step, kcal, distance ->
            sumStepModel.sumStep = step
            sumStepModel.sumKcal = kcal
            sumStepModel.sumDistance = distance

            sourceList.get(0).dataSource = gson.toJson(HomeRealtimeBean(true,ht))
            sourceList.get(1).dataSource = gson.toJson(sumStepModel)
            homeUiAdapter?.notifyItemChanged(0)
            homeUiAdapter?.notifyItemChanged(1)

        }

        showUIData()

    }


    private fun showEmptyData(){
        sourceList.clear()

        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_REAL_HR,null))

        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_STEP,null))
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_DETAIL_HR,null))

        //组装睡眠数据
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_SLEEP,null))

        //血氧
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_SPO2,null))
        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_BP,null))

        sourceList.add(HomeSourceBean(HomeDateType.HOME_TYPE_SPORT_RECORD,null))

        homeUiAdapter?.notifyDataSetChanged()
    }


    override fun onActivityResume() {
        super.onActivityResume()
        showDeviceStatus();

        //查询数据库中的数据
        getDataForDb()
    }


    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        showDeviceStatus();

        //查询数据库中的数据
        getDataForDb()
    }


    private fun showUIData(){
        //血压
        viewModel.singleBp.observe(viewLifecycleOwner){
            val str = sourceList.get(5).dataSource
            var singleBpModel : SingleBpModel ?= null
            if(str == null){
                singleBpModel = SingleBpModel()
            }else{
               singleBpModel = GsonUtils.getGsonObject<SingleBpModel>(str)
            }

            if(singleBpModel == null)
                singleBpModel = SingleBpModel()
            if(it == null)
                return@observe
            singleBpModel.sysBp = it.sysBp
            singleBpModel.diastolicBp = it.diastolicBp
            singleBpModel.saveLongTime = it.saveLongTime
            sourceList.get(5).dataSource = gson.toJson(singleBpModel)
            homeUiAdapter?.notifyItemChanged(5)
        }

        //血氧
        viewModel.singleSpo2.observe(viewLifecycleOwner){
            if(it == null)
                return@observe
            val str = sourceList.get(4).dataSource
            var singleBpSpo2 : SingleSpo2Model ?= null
            if(str == null){
                singleBpSpo2 = SingleSpo2Model()
            }else{
                singleBpSpo2 = GsonUtils.getGsonObject<SingleSpo2Model>(str)
            }
            if(singleBpSpo2 == null)
                singleBpSpo2 = SingleSpo2Model()

            singleBpSpo2.spo2Value = it.spo2Value
            singleBpSpo2.saveLongTime = it.saveLongTime
            sourceList.get(4).dataSource = gson.toJson(singleBpSpo2)
            homeUiAdapter?.notifyItemChanged(4)
        }

        //心率 详细
        viewModel.detailHr.observe(viewLifecycleOwner){
            Timber.e("-------心率="+gson.toJson(it))
            if(homeHrB == null){
                homeHrB = HomeHeartBean()
            }
            homeHrB?.dayStr = it.dayStr
           // homeHrB?.singleHr = it.singleHr
           // homeHrB?.singleHrTime = it.singleHrTime
            homeHrB?.hrList = it.heartList
           // homeHrB?.detailHrTime = it.detailHrTime
            sourceList[2].dataSource = gson.toJson(homeHrB)
            homeUiAdapter?.notifyItemChanged(2)
        }

        //锻炼数据
        viewModel.todayExercise.observe(viewLifecycleOwner){
            Timber.e("----homeduanl ="+gson.toJson(it))
            sourceList.get(6).dataSource = it.toString()
            homeUiAdapter?.notifyItemChanged(6)
        }

        //睡眠数据
        viewModel.lastRecordSleep.observe(viewLifecycleOwner){
            if(it == null){
                return@observe
            }

            val sleepStr = gson.toJson(it)
            sourceList.get(3).dataSource = sleepStr
            homeUiAdapter?.notifyItemChanged(3)
        }

    }



    private var homeHrB : HomeHeartBean ?=null

    private fun getDataForDb(){
        val mac = DBManager.getBindMac()
        if(BikeUtils.isEmpty(mac)){
            showEmptyData()
            return
        }

        //血压
        viewModel.queryLastSingleBp(mac)
        //血氧
        viewModel.queryLastSingleSpo2(mac)
        //心率 单次+详细
        viewModel.queryLastDetailHr(mac)

        //锻炼数据
        viewModel.getTodayExerciseData(mac,attachActivity)
        /**
         * 获取最近一次的睡眠数据
         */
        viewModel.getSleepForLastDay(mac)
    }


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
        if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED){
            homeRefreshLayout.setEnableRefresh(true)
        }
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
                    getDataForDb()
                }
            }

            if(action == BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION){
                if(homeRefreshLayout.isRefreshing){
                    homeRefreshLayout.finishRefresh()
                }
                homeDeviceStatusView.setHomeConnStatus(BaseApplication.getInstance().connStatus)

                getDataForDb()
            }
        }

    }



    override fun onRefresh(refreshLayout: RefreshLayout) {
        if(BaseApplication.getInstance().connStatus == ConnStatus.IS_SYNC_DATA){
            homeRefreshLayout.setEnableRefresh(false)
            homeRefreshLayout.finishRefresh()
            return
        }

        if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED){
            homeRefreshLayout.setEnableRefresh(false)
            homeRefreshLayout.finishRefresh()
            return
        }
        homeRefreshLayout.setEnableRefresh(true)

        DataOperateManager.getInstance(attachActivity).readAllDataSet(BleOperateManager.getInstance(),true)
        homeDeviceStatusView.setHomeConnStatus(BaseApplication.getInstance().connStatus)
    }
}