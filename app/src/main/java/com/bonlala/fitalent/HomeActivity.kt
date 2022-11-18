package com.bonlala.fitalent

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blala.blalable.AppUtils
import com.blala.blalable.BleConstant
import com.blala.blalable.BleOperateManager
import com.bonlala.action.ActivityManager
import com.bonlala.action.AppActivity
import com.bonlala.action.AppFragment
import com.bonlala.base.FragmentPagerAdapter
import com.bonlala.fitalent.activity.DfuActivity
import com.bonlala.fitalent.activity.ShowWebActivity
import com.bonlala.fitalent.adapter.NavigationAdapter
import com.bonlala.fitalent.dialog.AppUpdateDialog
import com.bonlala.fitalent.dialog.ConnTimeOutDialogView
import com.bonlala.fitalent.dialog.DfuUpgradeDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.http.api.AppVersionApi
import com.bonlala.fitalent.ui.dashboard.DashboardFragment
import com.bonlala.fitalent.ui.home.HomeFragment
import com.bonlala.fitalent.ui.notifications.NotificationsFragment
import com.bonlala.fitalent.utils.BikeUtils
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
        registerReceiver(broadcastReceiver, intentFilter)

        homeConnStateImgView.setOnClickListener {
            showConnTimeOutDialog()
        }
    }



    override fun initData() {


        mPagerAdapter = FragmentPagerAdapter(this)
        mPagerAdapter?.addFragment(HomeFragment().newInstance())
        mPagerAdapter?.addFragment(DashboardFragment().getInstance())

        mPagerAdapter?.addFragment(NotificationsFragment().getInstance())
        mViewPager?.adapter = mPagerAdapter

        onNewIntent(intent)
        val mac = MmkvUtils.getConnDeviceMac()
        if (!BikeUtils.isEmpty(mac)) {
            onNavigationItemSelected(0)
            switchFragment(0)
        } else {
            onNavigationItemSelected(1)
            switchFragment(1)
        }

        autoConnDevice()

        verticalDeviceVersion()

    }


    private fun verticalDeviceVersion() {
        viewModel.getAppVersion(this)
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
        viewModel.getNotConnUrl(this)
    }

    //重连
    public fun autoConnDevice() {
        try {
            val mac = MmkvUtils.getConnDeviceMac()
            Timber.e("----重新连接了=" + mac)
            if (!BikeUtils.isEmpty(mac)) {

                if (BaseApplication.getInstance().connStatus == ConnStatus.NOT_CONNECTED || BaseApplication.getInstance().connStatus == ConnStatus.CONNECTING) {
                    if (XXPermissions.isGranted(
                            this@HomeActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
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
            if (action.equals(BleConstant.BLE_SEND_DUF_VERSION_ACTION) ) {
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
}