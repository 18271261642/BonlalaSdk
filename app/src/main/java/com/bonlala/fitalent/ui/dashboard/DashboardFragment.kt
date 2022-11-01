package com.bonlala.fitalent.ui.dashboard


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.blala.blalable.BleConstant
import com.blala.blalable.BleOperateManager
import com.blala.blalable.blebean.CommBleSetBean
import com.bonlala.action.SingleClick
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.MainActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.*
import com.bonlala.fitalent.ble.DataOperateManager
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DeviceSetModel
import com.bonlala.fitalent.dialog.HelpDialogView
import com.bonlala.fitalent.dialog.SelectDialogView
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.ClickUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.item_device_top_layout.*
import kotlinx.android.synthetic.main.layout_device_empty_layout.*
import timber.log.Timber


/**
 * 设备设置页面
 */
 public class DashboardFragment : TitleBarFragment<HomeActivity>(), View.OnClickListener {

    private val tags = "DashboardFragment"

    fun getInstance() : DashboardFragment{
        return DashboardFragment()
    }


    private var bleOperateManager: BleOperateManager? = null

    private val viewModel by viewModels<DashboardViewModel>()

    private var deviceSetModel : DeviceSetModel ? = null

    override fun isStatusBarEnabled(): Boolean {
        return super.isStatusBarEnabled()
    }

    override fun isStatusBarDarkFont(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SYNC_COMPLETE_SET_ACTION)
        intentFilter.addAction(BleConstant.COMM_BROADCAST_ACTION)
        intentFilter.addAction(BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION)

        activity?.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        Timber.e("-----isFirst="+first)
        showReadSet()
    }

    override fun onActivityResume() {
        super.onActivityResume()
        showReadSet()
      Timber.e("------onActivty=")
    }



    override fun initData() {


        //读取电量
        if (BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED)
            return

        //getBattery()
        viewModel.byteArray.observe(viewLifecycleOwner) {

        }

    }


    //展示读取的设置
    @SuppressLint("SetTextI18n")
    private fun showReadSet(){
        val saveMac = MmkvUtils.getConnDeviceMac();
        if(!BikeUtils.isEmpty(saveMac)){
            deviceSetModel = DBManager.getInstance().getDeviceSetModel("user_1001",saveMac)
        }
        if(deviceSetModel == null)
            return

        Timber.e("----deviceModel="+Gson().toJson(deviceSetModel))
        //24小时心率
        realHeartCheckView.setCheckStatus(deviceSetModel!!.isIs24Heart)
        //电量
        setBatteryTv.text =  resources.getString(R.string.string_battery)+":"+ (if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.IS_SYNC_DATA) deviceSetModel!!.battery.toString()+"%" else "--")
        //计步目标
        menuStepGoalLayout.rightText = deviceSetModel!!.stepGoal.toString()+resources.getString(R.string.string_step)
        //亮屏时间
        menuScreenTimeBar.rightText = deviceSetModel!!.lightTime.toString()+resources.getString(R.string.string_second)
        //亮度等级
        menuScreenBrightnessBar.rightText = String.format(resources.getString(R.string.string_string_level),deviceSetModel!!.lightLevel)
        //公英制
        menuUnitBar.rightText = if(deviceSetModel!!.isKmUnit == 0) resources.getString(R.string.string_metric) else resources.getString(R.string.string_imperial)
        //温度单位
        menuTempUnitBar.rightText = if(deviceSetModel!!.tempStyle==0) "℃" else "℉"
        //固件版本
        menuFirmwareBar.rightText = deviceSetModel!!.deviceVersionName
        //转腕亮屏
        menuSwitchLightBar.rightText = if(deviceSetModel!!.turnWrist=="0") "未开启" else deviceSetModel!!.turnWrist
        //勿扰
        menuDntBar.rightText = if(deviceSetModel!!.dnt == "0") "未开启" else deviceSetModel!!.dnt
        //久坐
        menuLongSitBar.rightText = if(deviceSetModel!!.longSitStr == "0") "未开启" else deviceSetModel!!.longSitStr

    }


    //获取电量，连接成功后获取一次
    private fun getBattery() {
//        viewModel.batteryStr.observe(viewLifecycleOwner) {
//            setBatteryTv.text = it
//        }
//        bleOperateManager?.let { viewModel.getDeviceBattery(it) }
        viewModel.commSetModel.observe(viewLifecycleOwner){
            Log.e(tags,"------设置   ="+it.toString())
            deviceSetModel = it
            showReadSet()
        }
        val mac = MmkvUtils.getConnDeviceMac()

        //viewModel.readAllSetData(bleOperateManager!!,"user_1001",mac)
        DataOperateManager.getInstance(activity).readAllDataSet(bleOperateManager)
    }


    override fun onResume() {
        super.onResume()
        showConnStatus()
    }


    //连接状态
    private fun showConnStatus() {


        //判断是否已经连接过，没有连接过显示添加按钮
        val isSaveBle = MmkvUtils.getConnDeviceName()
        val isConn = BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.IS_SYNC_DATA
        setIsCanClick(!BikeUtils.isEmpty(isSaveBle) && isConn)

        menuDeviceNameTv.text = MmkvUtils.getConnDeviceName()
        devicePlaceHolderScrollView.visibility = if(BikeUtils.isEmpty(isSaveBle)) View.GONE else View.VISIBLE

        deviceHolderLayout.visibility = if(BikeUtils.isEmpty(isSaveBle)) View.VISIBLE else View.GONE

        deviceGuideImg.visibility = if(BikeUtils.isEmpty(isSaveBle)) View.GONE else View.VISIBLE

        //展示是否连接
        menuConnStatusTv.text =
            showStatus(BaseApplication.getInstance().connStatus)
        //未连接状态下显示重新连接
        reConnTv.visibility = if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.IS_SYNC_DATA) View.GONE else View.VISIBLE

        //setIsCanClick(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED)
    }


    private fun showStatus(connStatus: ConnStatus) : String? {
        var status: String? = null
        if (connStatus == ConnStatus.CONNECTED || connStatus == ConnStatus.IS_SYNC_COMPLETE) {
            status = resources.getString(R.string.string_connected)
        }
        if (connStatus == ConnStatus.CONNECTING) {
            status = resources.getString(R.string.string_conning)
        }
        if (connStatus == ConnStatus.NOT_CONNECTED) {
            status = resources.getString(R.string.string_not_connect)
        }
        if (connStatus == ConnStatus.IS_SYNC_DATA) {
            status = "Data synchronization…"
        }
        return status
    }

    //是否可以点击，未连接不可以点击
    private fun setIsCanClick(isCan: Boolean) {
        val normalColor = R.color.black
        val notConnColor = R.color.not_conn_txt_color
        commSetTv.setTextColor(resources.getColor(if(isCan) normalColor else notConnColor))
        functionSetTv.setTextColor(resources.getColor(if(isCan) normalColor else notConnColor))
        notifySetTv.setTextColor(resources.getColor(if(isCan) normalColor else notConnColor))
        generalSetTv.setTextColor(resources.getColor(if(isCan) normalColor else notConnColor))


        commNoConnLayout.visibility = if(isCan) View.GONE else View.VISIBLE
        functionNoConnLayout.visibility = if(isCan) View.GONE else View.VISIBLE
        notifyNoConnLayout.visibility = if(isCan) View.GONE else View.VISIBLE
        generalNoConnLayout.visibility = if(isCan) View.GONE else View.VISIBLE
        if(!isCan){
            commNoConnLayout.setOnClickListener(null)
            functionNoConnLayout.setOnClickListener(null)
            notifyNoConnLayout.setOnClickListener(null)
            generalNoConnLayout.setOnClickListener(null)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(broadcastReceiver)
    }

    @SingleClick
    override fun onClick(p0: View?) {
        val id = p0?.id

        when (id) {
            R.id.deviceGuideImg->{  //引导
                showHelpDialog()
            }

            //天气
            R.id.deviceWeatherLayout->{
                startActivity(WeatherActivity::class.java)
            }

            R.id.deviceAddDeviceTv->{ //添加设备
                startActivity(Intent(activity, MainActivity::class.java))
            }
            R.id.menuUnBindTv -> {    //解除绑定
                unBindDevice()
            }
            //重新连接
            R.id.reConnTv->{
                val isSaveBle = MmkvUtils.getConnDeviceMac()
                if(BikeUtils.isEmpty(isSaveBle))
                    return
                BaseApplication.getInstance().connStatusService.autoConnDevice(isSaveBle)
                reConnTv.text = resources.getString(R.string.string_conning)
            }

            R.id.deviceFindDeviceLayout -> {    //查找手机
                bleOperateManager?.findDevice()

            }

            //闹钟
            R.id.deviceAlarmLayout->{
                startActivity(AlarmListActivity::class.java)
            }

            //拍照
            R.id.deviceShutterLayout->{
                startActivity(Intent(activity,CamaraActivity::class.java))
            }

            //表盘
            R.id.deviceWatchDialLayout->{
                startActivity(DialActivity::class.java)
            }
            R.id.menuStepGoalLayout -> {
                var chooseData = 8000
              if(deviceSetModel != null){
                  chooseData = deviceSetModel!!.stepGoal
              }
              showChooseItem(0,resources.getString(R.string.string_step_target),resources.getString(R.string.string_step),true,chooseData)
            }

            //转腕亮屏
            R.id.menuSwitchLightBar -> {
                startActivity(Intent(activity, TurnWristActivity::class.java))
            }

            //亮屏时长
            R.id.menuScreenTimeBar->{
                var chooseData = 5
                if(deviceSetModel != null){
                    chooseData = deviceSetModel!!.lightTime
                }
                showChooseItem(1,resources.getString(R.string.string_bright_screen_time),resources.getString(R.string.string_second),true,chooseData)
            }

            //亮度等级
            R.id.menuScreenBrightnessBar->{
                var chooseData = 2
                if(deviceSetModel != null){
                    chooseData = deviceSetModel!!.lightLevel
                }
                showChooseItem(0x2,resources.getString(R.string.string_bright_level),resources.getString(R.string.string_string_level_str),true,chooseData)
            }
            //久坐提醒
            R.id.menuLongSitBar -> {
                startActivity(Intent(activity, LongSitActivity::class.java))
            }
            //勿扰
            R.id.menuDntBar -> {
                startActivity(Intent(activity, DNTActivity::class.java))
            }
            //公英制
            R.id.menuUnitBar->{
                var chooseData = 0
                if(deviceSetModel != null){
                    chooseData = deviceSetModel!!.isKmUnit
                }
                showChooseItem(0x04,resources.getString(R.string.string_temp_unit),"秒",false,chooseData)
            }
            R.id.menuTempUnitBar->{ //温度单位
                var chooseData = 0
                if(deviceSetModel != null){
                    chooseData = deviceSetModel!!.tempStyle
                }
                showChooseItem(0x03,resources.getString(R.string.string_temp_unit),"秒",false,chooseData)
            }

            //固件升级
            R.id.menuFirmwareBar->{
                startActivity(DfuActivity::class.java)
            }

            //消息提醒
            R.id.menuMsgNotifyBar->{
                startActivity(MsgNotifyActivity::class.java)
            }
        }
    }


    private fun showHelpDialog(){
        val helpDialog = HelpDialogView(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        helpDialog.show()
        val window = helpDialog.window
        val windowLayout = window?.attributes
        windowLayout?.gravity = Gravity.BOTTOM
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels
        windowLayout?.width = widthW
        window?.attributes = windowLayout
    }


    private fun unBindDevice() {

        val saveMac = MmkvUtils.getConnDeviceMac()
        if (BikeUtils.isEmpty(saveMac))
            return
        val alert = AlertDialog.Builder(requireActivity())
            .setTitle(resources.getString(R.string.string_prompt))
            .setMessage("是否确定解除绑定?")
            .setPositiveButton(
                resources.getString(R.string.string_confirm)
            ) { p0, p1 ->
                p0.dismiss()
                MmkvUtils.saveConnDeviceMac("")
                MmkvUtils.saveConnDeviceName("")

                bleOperateManager?.disConnYakDevice()

                showConnStatus()

            }
            .setNegativeButton(resources.getString(R.string.string_cancel)) { p0, p1 ->
                p0.dismiss()
            }
        alert.create().show()

    }

    var dataSource = mutableListOf<String>()

    private fun showChooseItem(code : Int,title : String,unitStr : String,isShowUnit : Boolean,backFill : Any){
        if(ClickUtils.isFastDoubleClick())
            return
        Timber.e("--backFIll="+backFill)
        dataSource.clear()
        var defaultPosition = 0
        if(code == 0){  //计步目标
            for(i in 1 ..30){
                val value = i * 1000
                dataSource.add(value.toString())
                if(backFill == value){
                    defaultPosition = i
                }
            }
        }

        if(code == 1){  //亮屏时间长 3~10s
            for(i in 3..10){
                val value = i
                dataSource.add(value.toString())
                if(backFill == value){
                    defaultPosition = i-3
                }
            }
        }

        //亮度等级
        if(code == 0x02){
            for(i in 1..5){
                dataSource.add(i.toString())
                if(backFill == i){
                    defaultPosition = i-1
                }
            }
        }

        //温度单位
        if(code == 0x03){
            dataSource.add("℃")
            dataSource.add("℉ ")

       }

        //公英制
        if(code == 0x04){
            dataSource.add(resources.getString(R.string.string_metric))
            dataSource.add(resources.getString(R.string.string_imperial))
        }

        Log.e(tags,"------value="+dataSource.toString()+defaultPosition)
        val selectDialogView = SelectDialogView.Builder(activity,dataSource)
        selectDialogView.setTitleTx(title)
        selectDialogView.setUnitShow(isShowUnit,unitStr)
        selectDialogView.setDefaultSelect(defaultPosition)
        //selectDialogView.setSourceData(dataSource)
        selectDialogView.setSignalSelectListener {
            if(code == 0){  //目标步数
                MmkvUtils.saveStepGoal(it.toInt())
                menuStepGoalLayout.rightText = it+""
                deviceSetModel?.stepGoal = it.toInt()
                viewModel.setDeviceTargetStep(bleOperateManager!!,it.toInt())
            }
            if(code == 1){  //亮屏时间长
                menuScreenTimeBar.rightText = it+resources.getString(R.string.string_second)
                deviceSetModel?.lightTime = it.toInt()

                deviceSetModel?.let { it1 -> viewModel.setLightAndInterval(bleOperateManager!!,it1.lightLevel,it.toInt()) }

            }
            if(code == 0x02){   //亮度等级范围
                menuScreenBrightnessBar.rightText = String.format(resources.getString(R.string.string_string_level),it.toInt())
                deviceSetModel?.lightLevel = it.toInt()

                deviceSetModel?.lightTime?.let { it1 ->
                    viewModel.setLightAndInterval(bleOperateManager!!,it.toInt(),
                        it1
                    )
                }
            }

            if(code == 0x03){   //温度单位
                menuTempUnitBar.rightText = it
                val commBleSetBean = CommBleSetBean()
                commBleSetBean.temperature = if(it.toString()=="℃") 0 else 1
                deviceSetModel?.tempStyle = if(it.toString()=="℃") 0 else 1
                setCommSet()
            }
            if(code == 0x04){   //公英制
                menuUnitBar.rightText = it
                deviceSetModel?.isKmUnit = if(it.equals(resources.getString(R.string.string_metric))) 0 else 1
                MmkvUtils.saveUnit(it.equals(resources.getString(R.string.string_metric)))
                setCommSet()
            }
            saveSetData()

        }
        selectDialogView.show()
    }


    private fun setCommSet(){
        val commBleSetBean = CommBleSetBean()
        commBleSetBean.temperature = deviceSetModel?.tempStyle ?: 0
        commBleSetBean.metric = deviceSetModel?.isKmUnit!!
        commBleSetBean.timeType = 1
        commBleSetBean.language = 1
        commBleSetBean.is24Heart = if(deviceSetModel?.isIs24Heart == true) 0 else 1
        viewModel.setCommSet(bleOperateManager!!,commBleSetBean)
    }



    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action ?: return
            if (action == BleConstant.BLE_CONNECTED_ACTION) {    //连接成功
                showConnStatus()
                getBattery()
            }

            if(action == BleConstant.BLE_SYNC_COMPLETE_SET_ACTION){ //设置同步完了
                showReadSet()
            }

            if(action == BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION){
                showConnStatus()
            }
        }

    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_dashboard
    }

    override fun initView() {

        realHeartCheckView.setLeftTitle(resources.getString(R.string.string_real_heart))
        phoneCallCheckView.setLeftTitle(resources.getString(R.string.string_phone_call))

        //24小时心率
        realHeartCheckView.setCheckListener { button, checked ->
            deviceSetModel?.isIs24Heart = checked
            setCommSet()
            saveSetData()
        }


        phoneCallCheckView.setCheckListener{button,checked->

        }

        menuMsgNotifyBar.setOnClickListener(this)
        menuUnitBar.setOnClickListener(this)
        deviceFindDeviceLayout?.setOnClickListener(this)
        deviceAlarmLayout.setOnClickListener(this)
        reConnTv.setOnClickListener(this)
        menuUnBindTv?.setOnClickListener(this)
        menuScreenTimeBar.setOnClickListener(this)
        deviceShutterLayout.setOnClickListener(this)
        menuStepGoalLayout?.setOnClickListener(this)
        menuSwitchLightBar?.setOnClickListener(this)
        menuLongSitBar?.setOnClickListener(this)
        menuDntBar?.setOnClickListener(this)
        reConnTv.setOnClickListener(this)
        deviceAddDeviceTv.setOnClickListener(this)
        menuTempUnitBar.setOnClickListener(this)
        deviceWeatherLayout.setOnClickListener(this)
        deviceGuideImg.setOnClickListener ( this )
        menuScreenBrightnessBar.setOnClickListener(this)
        deviceWatchDialLayout.setOnClickListener(this)
        menuFirmwareBar.setOnClickListener(this)

        bleOperateManager = BaseApplication.getInstance().bleOperate;
    }


    //保存
    private fun saveSetData(){
        val mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac))
            return
        DBManager.getInstance().saveDeviceSetData("user_1001",mac,deviceSetModel)
    }


}