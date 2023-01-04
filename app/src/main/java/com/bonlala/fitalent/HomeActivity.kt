package com.bonlala.fitalent

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blala.blalable.AppUtils
import com.blala.blalable.BleConstant
import com.bonlala.action.ActivityManager
import com.bonlala.action.AppActivity
import com.bonlala.action.AppFragment
import com.bonlala.base.FragmentPagerAdapter
import com.bonlala.fitalent.activity.DfuActivity
import com.bonlala.fitalent.activity.ShowWebActivity
import com.bonlala.fitalent.adapter.NavigationAdapter
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.dialog.AppUpdateDialog
import com.bonlala.fitalent.dialog.ConnTimeOutDialogView
import com.bonlala.fitalent.dialog.DfuUpgradeDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.DeviceType
import com.bonlala.fitalent.http.api.AppVersionApi
import com.bonlala.fitalent.ui.dashboard.DashboardFragment
import com.bonlala.fitalent.ui.home.HomeFragment
import com.bonlala.fitalent.ui.home.HrBeltHomeFragment
import com.bonlala.fitalent.ui.notifications.NotificationsFragment
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.BonlalaUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.HomeActivityViewModel
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber


/**主页HomeActivity**/
class HomeActivity : AppActivity(), NavigationAdapter.OnNavigationListener {

    private val viewModel by viewModels<HomeActivityViewModel>()

    private val INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex"
    private val INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass"

    private var mViewPager: ViewPager? = null
    private var mNavigationView: RecyclerView? = null

    private var mNavigationAdapter: NavigationAdapter? = null
    private var mPagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null

    //是否显示断连提醒的图表
    private var isShowConnImg = false

    //连接不上点击进入的指引url
    var noConnDescUrl : String ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun initView() {
        mViewPager = findViewById(R.id.vp_home_pager)
        mNavigationView = findViewById(R.id.rv_home_navigation)

        mNavigationAdapter = NavigationAdapter(this)

        //判断是否有绑定过，有绑定过进入首页，未绑定进入设置
        mNavigationAdapter!!.addItem(
            NavigationAdapter.MenuItem(
                getString(R.string.title_home),
                ContextCompat.getDrawable(this, R.drawable.home_home_selector)
            )
        )
        mNavigationAdapter!!.addItem(
            NavigationAdapter.MenuItem(
                getString(R.string.title_dashboard),
                ContextCompat.getDrawable(this, R.drawable.home_device_selector)
            )
        )

        mNavigationAdapter!!.addItem(
            NavigationAdapter.MenuItem(
                getString(R.string.title_notifications),
                ContextCompat.getDrawable(this, R.drawable.ic_home_mine_selector)
            )
        )

        mNavigationAdapter!!.setOnNavigationListener(this)
        mNavigationView!!.adapter = mNavigationAdapter

        val intentFilter = IntentFilter(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        intentFilter.addAction(BleConstant.BLE_SEND_DUF_VERSION_ACTION)
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)

        homeConnStateImgView.setOnClickListener {
            showConnTimeOutDialog()
        }

        mPagerAdapter = FragmentPagerAdapter(this)
    }



    override fun initData() {

        checkDeviceTypeFragment()

        onNewIntent(intent)


        autoConnDevice()

        verticalDeviceVersion()

    }


    //显示不同的设备类型fragment
    fun checkDeviceTypeFragment(){
        val type = DBManager.getBindDeviceType()

        mPagerAdapter?.clearFragment()

        Timber.e("------deviceType="+type)

        if(type == DeviceType.DEVICE_561){
            mPagerAdapter?.addFragment(HrBeltHomeFragment().getInstance())
        }else{
            mPagerAdapter?.addFragment(HomeFragment().newInstance())
        }

        mPagerAdapter?.addFragment(DashboardFragment().getInstance())

        mPagerAdapter?.addFragment(NotificationsFragment().getInstance())
        mViewPager?.adapter = mPagerAdapter

        //判断是否绑定过设备，绑定过设备默认展示首页，未绑定展示设备页面
        val mac = MmkvUtils.getConnDeviceMac()
        if (!BikeUtils.isEmpty(mac)) {
            onNavigationItemSelected(0)
            switchFragment(0)
        } else {
            onNavigationItemSelected(1)
            switchFragment(1)
        }




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Timber.e("-------homeActivityResult="+requestCode+" "+resultCode)

        //判断是否是打开了蓝牙
        if(requestCode == 1001){
            autoConnDevice()
        }else{
            checkDeviceTypeFragment()
        }

    }



    //验证设备的版本
    private fun verticalDeviceVersion() {

        viewModel.isShowDfuAlert.observe(this) {
            if (it == true) {
                showDeviceDfuDialog()

            }
        }

        viewModel.appVersion.observe(this) {
            if (it != null) {
                showAppDialog(it)
            }

        }

        viewModel.notConnDescUrl.observe(this){
            noConnDescUrl = it
        }
        viewModel.getAppVersion(this)
        viewModel.getNotConnUrl(this)
    }

    //重连
    public fun autoConnDevice() {
        try {
            val mac = MmkvUtils.getConnDeviceMac()
            Timber.e("----重新连接了=" + mac+" "+BaseApplication.getInstance().connStatus)
            if (!BikeUtils.isEmpty(mac)) {

                if (BaseApplication.getInstance().connStatus == ConnStatus.NOT_CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.CONNECTING) {

                    val isLocalPermission = ActivityCompat.checkSelfPermission(this@HomeActivity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                    Timber.e("-----权限是否打开了="+isLocalPermission)
                    //判断权限是否打开
                    if (!isLocalPermission)
                     {
                        //判断蓝牙是否打开，没有打开提醒打开蓝牙
                        //判断蓝牙是否打开
                        val isOpenBle = BonlalaUtils.isOpenBlue(this@HomeActivity)
                        Timber.e("-------isOpenBle="+isOpenBle)
                        if(!isOpenBle){
                            openBluetooth(this)
                            return
                        }

                         Timber.e("----isOpenBleisOpenBle-----")
                        val service = BaseApplication.getInstance().connStatusService
                        service?.autoConnDevice(mac, false)

                    } else {
                        XXPermissions.with(this@HomeActivity).permission(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        ).request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean
                            ) {
//                                if(all)
//                                    autoConnDevice()
                            }
                        })
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        switchFragment(mPagerAdapter!!.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager!!.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX))
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }

        Timber.e("-------swww=" + fragmentIndex)

        when (fragmentIndex) {
            0, 1, 2, 3 -> {
                mViewPager!!.currentItem = fragmentIndex
                mNavigationAdapter!!.selectedPosition = fragmentIndex
                if(fragmentIndex == 2){
                    showNotConnImg(false)
                }else{
                    showNotConnImg(isShowConnImg)
                }
            }
            else -> {}
        }
    }


    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2 -> {
                mViewPager!!.currentItem = position
                if(position == 2){
                    showNotConnImg(false)
                }else{
                    showNotConnImg(isShowConnImg)
                }
                true
            }
            else -> false
        }
    }


    //展示版本更新弹框
    private fun showAppDialog(appInfo: AppVersionApi.AppVersionInfo) {
        //获取app版本名称
        val appVersionName = AppUtils.getAppVersionName(this)
        if (appVersionName.equals(appInfo.versionName) || appInfo.updateMethod == 0) {

            return
        }

        val appDialog = AppUpdateDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        appDialog.show()
        appDialog.setCancelable(false)
        appDialog.showUpdateMsg(appInfo.versionName, appInfo.remark.toString())
        appDialog.setIsAwayUpdate(appInfo.updateMethod == 2)
        appDialog.setOnDownloadListener { position ->
            if (position == 0x00) {
                appDialog.dismiss()
            }

            if (position == 0x01) {
                appDialog.showStartUpdate(
                    "https://microdown.myapp.com/ug/20220930_7dc10288a8fbc98989abf0c5f2bc7138_sign_v1.apk",
                    this@HomeActivity
                )
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }


    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 过滤按键动作
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                mExitTime = System.currentTimeMillis()
                ToastUtils.show(resources.getString(R.string.string_double_click_exit))
                return true
            } else {
                BaseApplication.getInstance().bleOperate.disConnYakDevice()
                ActivityManager.getInstance().finishAllActivities()
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    //显示固件升级的弹窗，点击进入固件升级页面
    private fun showDeviceDfuDialog() {

        val dfuDialog = DfuUpgradeDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dfuDialog.show()
        dfuDialog.setOnItemClick {
            dfuDialog.dismiss()
            if (it == 0x01) {
                startActivity(DfuActivity::class.java)
            }
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            if (action.equals(BleConstant.BLE_SEND_DUF_VERSION_ACTION) || action.equals(BleConstant.BLE_CONNECTED_ACTION )) {
               // showNotConnImg(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus != ConnStatus.IS_SYNC_DATA)
                isShowConnImg = false
                val version = p1?.getStringExtra("comm_key")
                if (version != null) {
                    viewModel.getDeviceVersionInfo(this@HomeActivity, version)
                }
                showNotConnImg(false)
            }


            if(action.equals(BleConstant.BLE_SCAN_COMPLETE_ACTION)){
                val isConn = BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED || BaseApplication.getInstance().connStatus ==ConnStatus.IS_SYNC_DATA
                Timber.e("----isConn="+isConn)
                if(!isConn){
                    BaseApplication.getInstance().connStatus = ConnStatus.NOT_CONNECTED
                    isShowConnImg = true
                }else{
                    isShowConnImg = false
                }
                showNotConnImg(!isConn)
            }
        }

    }


    //是否显示断连提醒的图片
     fun showNotConnImg(isShow : Boolean){

        homeConnStateImgView.visibility = if(isShow) View.VISIBLE else View.GONE
        if(BaseApplication.getInstance().connStatus == ConnStatus.CONNECTED){
            homeConnStateImgView.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()

        Timber.e("------")
        if(BaseApplication.getInstance().isNeedReloadPage){
            BaseApplication.getInstance().isNeedReloadPage = false
            checkDeviceTypeFragment()
        }

    }

    private fun showConnTimeOutDialog() {
        val dialog =
            ConnTimeOutDialogView(this@HomeActivity, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setOnItemClick {
            dialog.dismiss()
           if(noConnDescUrl == null){
               return@setOnItemClick
           }
            startActivity(ShowWebActivity::class.java, arrayOf("url","title"), arrayOf(noConnDescUrl,resources.getString(R.string.string_device_cant_conn)))
        }
    }


    fun setImgStatus(isShow: Boolean){
        isShowConnImg = isShow
    }




    //显示和隐藏底部菜单，心率带使用
    fun setVisibilityBottomMenu(isShow : Boolean){
        mNavigationView?.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun isDestroyed(): Boolean {
        Timber.e("----isDestroyed---=")
        return super.isDestroyed()
    }

    //打开蓝牙
    fun openBluetooth(activity: Activity) {

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val isEnable = bluetoothAdapter.isEnabled
        Timber.e("-------蓝牙="+(bluetoothAdapter.isEnabled))
        if (bluetoothAdapter != null && !isEnable) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            Timber.e("--------111111111111")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("--------22222")
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    XXPermissions.with(activity).permission(Manifest.permission.BLUETOOTH_CONNECT)
                        .request { permissions, all -> }
                }else{
                    startActivityForResult(enableBtIntent,1001)
                }

                return
            }

            startActivityForResult(enableBtIntent,1001)
        }

    }

}