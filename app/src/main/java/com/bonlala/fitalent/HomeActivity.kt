package com.bonlala.fitalent

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blala.blalable.AppUtils
import com.bonlala.action.ActivityManager
import com.bonlala.action.AppActivity
import com.bonlala.action.AppFragment
import com.bonlala.base.FragmentPagerAdapter
import com.bonlala.fitalent.adapter.NavigationAdapter
import com.bonlala.fitalent.dialog.AppUpdateDialog
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.http.api.AppVersionApi
import com.bonlala.fitalent.ui.dashboard.DashboardFragment
import com.bonlala.fitalent.ui.home.HomeFragment
import com.bonlala.fitalent.ui.notifications.NotificationsFragment
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.HomeActivityViewModel
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import org.json.JSONObject
import timber.log.Timber


/**主页HomeActivity**/
class HomeActivity : AppActivity() , NavigationAdapter.OnNavigationListener{

    private val viewModel by viewModels<HomeActivityViewModel>()

    private val INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex"
    private val INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass"

    private var mViewPager: ViewPager? = null
    private var mNavigationView: RecyclerView? = null

    private var mNavigationAdapter: NavigationAdapter? = null
    private var mPagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null

    override fun getLayoutId(): Int {
      return R.layout.activity_home
    }

    override fun initView() {
        mViewPager = findViewById(R.id.vp_home_pager)
        mNavigationView = findViewById(R.id.rv_home_navigation)

        mNavigationAdapter = NavigationAdapter(this)
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


        viewModel.appVersion.observe(this){
            if (it != null) {
                showAppDialog(it)
            }

        }
    }

    override fun initData() {
        getH5Url()
        mPagerAdapter = FragmentPagerAdapter(this)
        mPagerAdapter?.addFragment(HomeFragment().newInstance())
        mPagerAdapter?.addFragment(DashboardFragment().getInstance())
        mPagerAdapter?.addFragment(NotificationsFragment().getInstance())
        mViewPager?.adapter = mPagerAdapter

        onNewIntent(intent)

        autoConnDevice()
        viewModel.getAppVersion(this)

    }



    //重连
    private fun autoConnDevice(){

        try {
            val mac = MmkvUtils.getConnDeviceMac()
            if(!BikeUtils.isEmpty(mac)){
                if(BaseApplication.getInstance().connStatus == ConnStatus.NOT_CONNECTED){
                    if(XXPermissions.isGranted(this@HomeActivity,Manifest.permission.ACCESS_FINE_LOCATION)){
                        BaseApplication.getInstance().connStatusService.autoConnDevice(mac)
                    }else{
                        XXPermissions.with(this@HomeActivity).permission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)).request(object : OnPermissionCallback{
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean
                            ) {
                                if(all)
                                    autoConnDevice()
                            }
                        })
                    }
                }
            }
        }catch (e : Exception){
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
        when (fragmentIndex) {
            0, 1, 2, 3 -> {
                mViewPager!!.currentItem = fragmentIndex
                mNavigationAdapter!!.selectedPosition = fragmentIndex
            }
            else -> {}
        }
    }


    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2 -> {
                mViewPager!!.currentItem = position
                true
            }
            else -> false
        }
    }


    //获取H5链接
    private fun getH5Url(){
        EasyHttp.get(this).api("api/app/common/h5urls").request(object : OnHttpListener<String>{

            override fun onSucceed(result: String?) {
                if(result == null)
                    return
                try {
                    val jsonObject = JSONObject(result)
                    val dataJsonObject = jsonObject.getJSONObject("data");
                    if(dataJsonObject == null)
                        return
                    val userAgreementUrl = dataJsonObject.getString("userAgreementUrl")
                    MmkvUtils.saveUserAgreement(userAgreementUrl)
                    val privacyUrl = dataJsonObject.getString("privacyAgreementUrl")
                    MmkvUtils.savePrivacyUrl(privacyUrl)

                    val deviceGuideUrl = dataJsonObject.getString("deviceGuideUrl")

                    MmkvUtils.saveGuideUrl(deviceGuideUrl)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }

            override fun onFail(e: Exception?) {
                Timber.e("-----ee="+e?.message)
            }

        })
    }





    //展示版本更新弹框
   private fun showAppDialog(appInfo : AppVersionApi.AppVersionInfo){
        //获取app版本名称
        val appVersionName = AppUtils.getAppVersionName(this)
        if(appVersionName.equals(appInfo.versionName) || appInfo.updateMethod == 0){

            return
        }

       val appDialog = AppUpdateDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        appDialog.show()
        appDialog.setCancelable(false)
        appDialog.showUpdateMsg(appInfo.versionName,appInfo.remark.toString())
        appDialog.setIsAwayUpdate(appInfo.updateMethod == 2)
        appDialog.setOnDownloadListener { position ->
            if (position == 0x00) {
                appDialog.dismiss()
            }

            if (position == 0x01) {
                appDialog.showStartUpdate("https://microdown.myapp.com/ug/20220930_7dc10288a8fbc98989abf0c5f2bc7138_sign_v1.apk", this@HomeActivity)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

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
}