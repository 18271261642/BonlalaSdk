package com.bonlala.fitalent.activity

import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.blala.blalable.BleOperateManager
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.action.ActivityManager
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.ble.DataOperateManager
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.dialog.CommAlertDialogView
import com.bonlala.fitalent.http.api.VersionApi
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.service.DfuService
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.DfuViewModel
import com.hjq.http.listener.OnDownloadListener
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_dfu_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * W560B固件升级
 * Created by Admin
 *Date 2022/9/19
 */
class DfuActivity : AppActivity() {

    private val viewModel by viewModels<DfuViewModel>()

    //手表的固件版本
    private var deviceVersion : String ?= null

    //下载保存文件的路径
    private var fileSaveUrl : String ?= null
    //W560B的固件包
    private val w560BDfuFile = "w560b_bin.bin"

    //是否在升级中，升级中进制退出
    private var isUpgradeing : Boolean ?= null

    //是否已下载，没有下载显示下载按钮，下载完成后隐藏下载按钮
    private var isDownloadComplete : Boolean ?= null

    //后台获取的固件版本，用于下载地址使用
    private var serverVersionInfo: VersionApi.VersionInfo ? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_dfu_layout
    }

    override fun initView() {
        isDownloadComplete = false



        //点击开始下载
        dfuCompleteTv.setOnClickListener {
            if(serverVersionInfo == null)
                return@setOnClickListener
            if(isUpgradeing == true){
                ToastUtils.show(resources.getString(R.string.string_upgrade_ing_not_cancel))
                return@setOnClickListener
            }

            startDownload(serverVersionInfo!!)
        }

        //下载完成，点击开始升级
        dfuDownloadTv.setOnClickListener {
            if(serverVersionInfo == null)
                return@setOnClickListener

            startDfu()
        }

        dfuBtnStatusView.setOnClickListener {
           finish()
        }

    }



    //判断是否满足条件升级
    private fun verticalUpgradeFunction() {
        BleOperateManager.getInstance().readBattery(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
               val batteryValue = value?.get(0)?.toInt()
                Timber.e("-----battery="+batteryValue)
                dfuBatteryTv.text = batteryValue.toString()+"%"
                if (batteryValue != null) {
                    dfuBatteryView.power = batteryValue.toInt()
                }
                if (batteryValue != null) {
                    if(batteryValue.toInt()<40){
                        isUpgradeing = true
                        showNotCancel(resources.getString(R.string.string_low_battery_alert),true)
                        return
                    }
                }
            }

            override fun onStrDataBack(vararg value: String?) {

            }

        })
    }

    override fun initData() {
        dfuNoUpdateTv.visibility = View.VISIBLE
        dfuBtnStatusView.visibility = View.VISIBLE
        dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_i_konw)
        dfuBtnStatusView.mbgColor = Color.parseColor("#FF4EDD7D")


        dfuCompleteTv.visibility = View.GONE
        dfuDownloadTv.visibility = View.GONE

        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener)
        fileSaveUrl = getExternalFilesDir(null)?.path
        Timber.e("---path="+fileSaveUrl)
        viewModel.getServerData.observe(this){
            if (it != null) {
                showUpdateContent(it)
            }
        }

        viewModel.deviceDfuVersion.observe(this){
            deviceVersion = it
            dfuCurrentVersionTv.text = resources.getString(R.string.string_current_version)+it.toString()


            //获取后台固件版本信息
            viewModel.getServerVersionInfo(this)
        }
        viewModel.getDeviceVersion(BleOperateManager.getInstance())


        verticalUpgradeFunction()
    }


    override fun onLeftClick(view: View?) {
        showNotCancel(resources.getString(R.string.string_upgrade_ing_not_cancel),false)
        super.onLeftClick(view)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 过滤按键动作
        if (event.keyCode == KeyEvent.KEYCODE_BACK && isUpgradeing == true) {
            showNotCancel(resources.getString(R.string.string_upgrade_ing_not_cancel),false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun showNotCancel(content : String,isFinish : Boolean){
        if(isUpgradeing == true){
            val dialog = CommAlertDialogView(this@DfuActivity, com.bonlala.base.R.style.BaseDialogTheme)
            dialog.show()
            dialog.setCancelable(false)
            dialog.setShowContent(content,true)
            dialog.setCommListener {
                dialog.dismiss()
                if(isFinish){
                    finish()
                }

            }
        }
    }

    //显示更新的内容
    private fun showUpdateContent(versionInfo: VersionApi.VersionInfo){
        serverVersionInfo = versionInfo
        //startDownload(versionInfo)
        val isE = (versionInfo.versionName.lowercase(Locale.ROOT) == ("v$deviceVersion").lowercase(
            Locale.ROOT))
        Timber.e("------版本="+versionInfo.versionName+" "+deviceVersion+" "+isE)
        //无需更新
        if(versionInfo.versionName.lowercase(Locale.ROOT) == (deviceVersion?.lowercase(Locale.ROOT))){
            dfuNoUpdateTv.visibility = View.VISIBLE
            dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_i_konw)
            dfuBtnStatusView.mbgColor = Color.parseColor("#FF4EDD7D")


            dfuCompleteTv.visibility = View.GONE
            dfuDownloadTv.visibility = View.GONE

            return
        }
        dfuCompleteTv.visibility = View.VISIBLE
        dfuDownloadTv.visibility = View.GONE
        dfuBtnStatusView.visibility = View.GONE

        dfuNoUpdateTv.visibility = View.GONE
        dfuNetLastVersionTv.text = resources.getString(R.string.string_last_version)+":"+versionInfo.versionName
        dfuFileSizeTv.text = resources.getString(R.string.string_version_file_size)+""+(versionInfo.fileSize/1000)+"kb"
        dfuRemarkTv.text = resources.getString(R.string.string_version_desc)+"\n"+versionInfo.remark

        dfuCompleteTv.text = resources.getString(R.string.string_string_download)


        isDownloadComplete = false
    }


    //开始下载
    private fun startDownload(versionInfo: VersionApi.VersionInfo){
        dfuBtnStatusView.isDownload = true
        titleBar?.leftIcon = null
        //判断是否有存储权限
        downloadFile(versionInfo.fileUrl, "$fileSaveUrl/$w560BDfuFile",object : OnDownloadListener{
            override fun onStart(file: File?) {
                Timber.e("----开始下载")
                dfuCompleteTv.visibility = View.GONE
                dfuDownloadTv.visibility = View.VISIBLE
                isUpgradeing = true

                dfuDownloadTv.allScheduleValue = 100f
                dfuDownloadTv.showTxt = resources.getString(R.string.string_download_ing)
            }

            override fun onProgress(file: File?, progress: Int) {
                Timber.e("----onProgress="+progress)

                dfuDownloadTv.currScheduleValue = progress.toFloat()
                dfuDownloadTv.showTxt = resources.getString(R.string.string_download_ing)+":"+progress+"%"

            }

            override fun onComplete(file: File?) {
                Timber.e("----onComplete="+file?.path)

                ToastUtils.show(resources.getString(R.string.string_download_success))
                dfuCompleteTv.visibility = View.GONE
                dfuDownloadTv.visibility = View.VISIBLE
                dfuDownloadTv.currScheduleValue = 100f
                dfuDownloadTv.showTxt = resources.getString(R.string.string_download_start_upgrade)
                isUpgradeing = false

                file?.path?.let {
                   // downloadCompleteAndToStart(it)
                }

            }

            override fun onError(file: File?, e: Exception?) {
                Timber.e("----onError="+e?.message)
                isUpgradeing = false
                titleBar?.leftIcon = resources.getDrawable(R.drawable.ic_black_back)
            }

            override fun onEnd(file: File?) {
                Timber.e("----onEnd="+file?.path)
            }

        })
    }



    //下载完成是否升级
    private fun downloadCompleteAndToStart(url : String){
        val dialog = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.string_prompt))
            .setMessage("下载完成，是否开始升级固件?")
            .setNegativeButton(resources.getString(R.string.string_cancel)
            ) { p0, p1 ->
                p0?.dismiss()
                finish()
            }
            .setPositiveButton(resources.getString(R.string.string_confirm)
            ) { p0, p1 ->
                p0?.dismiss()
                startDfu()
            }
        dialog.create().show()
    }



    //下载完成后开始升级
    private fun startDfu(){
        val url = "$fileSaveUrl/$w560BDfuFile"
        if(url == null)
            return
        val uri = Uri.fromFile(File("$fileSaveUrl/$w560BDfuFile"))
        Timber.e("---url="+url+"\n"+uri.toString())
        isUpgradeing = true

        dfuDownloadTv.visibility = View.GONE
        dfuBtnStatusView.visibility = View.VISIBLE
        dfuBtnStatusView.mbgColor = Color.parseColor("#FFD6D6DD")
        dfuBtnStatusView.setShowTxt = null
        dfuBtnStatusView.isDownload = true
        dfuBtnStatusView.showDownloadTxt = resources.getString(R.string.string_upgrade_ing_not_cancel)


        val mac = MmkvUtils.getConnDeviceMac()
        val dfuServiceInitiator = DfuServiceInitiator(mac)
            .setDeviceName(MmkvUtils.getConnDeviceName())
            .setKeepBond(true)
            .setForceDfu(false)
            .setPacketsReceiptNotificationsEnabled(true)
            .setPacketsReceiptNotificationsValue(6)
            .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        dfuServiceInitiator.disableResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(this)
        }
        dfuServiceInitiator.setZip(url)
        dfuServiceInitiator.start(BaseApplication.getInstance().applicationContext,DfuService::class.java)
    }


    override fun onDestroy() {
        super.onDestroy()
        //
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener)
    }

    private val mDfuProgressListener : DfuProgressListener = object : DfuProgressListener{
        override fun onDeviceConnecting(deviceAddress: String?) {
            Timber.e("------onDeviceConnecting--------")

            dfuBtnStatusView.mbgColor = Color.parseColor("#FFD6D6DD")
            dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_upgrade_ing)
        }

        override fun onDeviceConnected(deviceAddress: String?) {
            Timber.e("-------onDeviceConnected-------")
        }

        override fun onDfuProcessStarting(deviceAddress: String?) {
            Timber.e("------onDfuProcessStarting--------")
            isUpgradeing = true
            dfuDownloadTv.visibility = View.GONE
        }

        override fun onDfuProcessStarted(deviceAddress: String?) {
            Timber.e("------onDfuProcessStarted--------")
        }

        override fun onEnablingDfuMode(deviceAddress: String?) {
            Timber.e("-------onEnablingDfuMode-------")
        }

        override fun onProgressChanged(
            deviceAddress: String?,
            percent: Int,
            speed: Float,
            avgSpeed: Float,
            currentPart: Int,
            partsTotal: Int
        ) {
            Timber.e("------onProgressChanged--------="+percent+" "+currentPart)
            dfuBtnStatusView.mbgColor = Color.parseColor("#FFD6D6DD")
            dfuBtnStatusView.setShowTxt = null
            dfuBtnStatusView.isDownload = true
            dfuBtnStatusView.showDownloadTxt = resources.getString(R.string.string_upgrade_ing)+":"
            dfuBtnStatusView.setCurrentProgressValue(percent,100f)

        }

        override fun onFirmwareValidating(deviceAddress: String?) {
            Timber.e("-----onFirmwareValidating---------")
            isUpgradeing = false
            titleBar?.leftIcon = resources.getDrawable(R.drawable.ic_black_back)
        }

        override fun onDeviceDisconnecting(deviceAddress: String?) {
            Timber.e("-----onDeviceDisconnecting---------")
        }

        override fun onDeviceDisconnected(deviceAddress: String?) {
            Timber.e("----onDeviceDisconnected----------")
        }

        override fun onDfuCompleted(deviceAddress: String?) {
            Timber.e("-------onDfuCompleted-------="+deviceAddress)
            ToastUtils.show(resources.getString(R.string.string_upgrade_success))
            dfuNoUpdateTv.visibility = View.GONE
            dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_upgrade_success)
            dfuBtnStatusView.mbgColor = Color.parseColor("#FF4EDD7D")
            isUpgradeing = false
            titleBar?.leftIcon = resources.getDrawable(R.drawable.ic_black_back)

            val saveMac = MmkvUtils.getConnDeviceMac()
            if(!BikeUtils.isEmpty(saveMac)){
//                BaseApplication.getInstance().connStatusService.autoConnDevice(saveMac,true)
            }
            //BaseApplication.getInstance().connStatusService.autoConnDevice()
        }

        override fun onDfuAborted(deviceAddress: String?) {
            Timber.e("------onDfuAborted--------")
        }

        override fun onError(deviceAddress: String?, error: Int, errorType: Int, message: String?) {
            Timber.e("--------onError------="+error+" "+message)
            dfuBtnStatusView.isDownload = false
            dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_upgrade_failed)
            ToastUtils.show("升级失败,请重新升级!")
            isUpgradeing = false
            titleBar?.leftIcon = resources.getDrawable(R.drawable.ic_black_back)
        }

    }
}