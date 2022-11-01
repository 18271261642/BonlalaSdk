package com.bonlala.fitalent.activity

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import com.blala.blalable.BleOperateManager
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.http.api.VersionApi
import com.bonlala.fitalent.service.DfuService
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.DfuViewModel
import com.hjq.http.listener.OnDownloadListener
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_dfu_layout.*
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import timber.log.Timber
import java.io.File

/**
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

    override fun getLayoutId(): Int {
        return R.layout.activity_dfu_layout
    }

    override fun initView() {

        dfuBtnStatusView.setOnClickListener {
            ToastUtils.show("click")
        }
    }

    override fun initData() {
        dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_i_konw)
        dfuNoUpdateTv.visibility = View.VISIBLE


        fileSaveUrl = getExternalFilesDir(null)?.path
        Timber.e("---path="+fileSaveUrl)
        viewModel.getServerData.observe(this){
            if (it != null) {
                showUpdateContent(it)
            }
        }

        viewModel.deviceDfuVersion.observe(this){
            deviceVersion = it

            //获取后台固件版本信息
            viewModel.getServerVersionInfo(this)
        }
        viewModel.getDeviceVersion(BleOperateManager.getInstance())

        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener)
    }

    //显示更新的内容
    private fun showUpdateContent(versionInfo: VersionApi.VersionInfo){
//        startDownload(versionInfo)
        if(versionInfo.updateMethod == 0){
            dfuNoUpdateTv.visibility = View.VISIBLE
            dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_i_konw)
            dfuBtnStatusView.mbgColor = Color.parseColor("#FF4EDD7D")
            return
        }
        if(versionInfo.versionName.equals(deviceVersion)){
            dfuNoUpdateTv.visibility = View.VISIBLE
            dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_i_konw)
            dfuBtnStatusView.mbgColor = Color.parseColor("#FF4EDD7D")
            return
        }
        dfuNoUpdateTv.visibility = View.GONE
        dfuNetLastVersionTv.text = resources.getString(R.string.string_last_version)+""+versionInfo.versionName
        dfuFileSizeTv.text = resources.getString(R.string.string_version_file_size)+""+(versionInfo.fileSize/1000)+"kb"
        dfuRemarkTv.text = resources.getString(R.string.string_version_desc)+"\n"+versionInfo.remark

        dfuBtnStatusView.setShowTxt = resources.getString(R.string.string_string_download)
        dfuBtnStatusView.mbgColor = Color.parseColor("#FFD6D6DD")
    }


    //开始下载
    private fun startDownload(versionInfo: VersionApi.VersionInfo){
        dfuBtnStatusView.isDownload = true
        //判断是否有存储权限
        downloadFile(versionInfo.fileUrl, "$fileSaveUrl/$w560BDfuFile",object : OnDownloadListener{
            override fun onStart(file: File?) {
                Timber.e("----开始下载")
            }

            override fun onProgress(file: File?, progress: Int) {
                Timber.e("----onProgress="+progress)
                dfuBtnStatusView.setCurrentProgressValue(progress.toFloat(),100f)
            }

            override fun onComplete(file: File?) {
                Timber.e("----onComplete="+file?.path)
                file?.path?.let { startDfu(it) }

            }

            override fun onError(file: File?, e: Exception?) {
                Timber.e("----onError="+e?.message)
            }

            override fun onEnd(file: File?) {
                Timber.e("----onEnd="+file?.path)
            }

        })
    }


    //下载完成后开始升级
    private fun startDfu(binUrl : String){
        val url = "$fileSaveUrl/$w560BDfuFile"

        val uri = Uri.fromFile(File("$fileSaveUrl/$w560BDfuFile"))
        Timber.e("---url="+url+"\n"+uri.toString())

        val mac = MmkvUtils.getConnDeviceMac()
        val dfuServiceInitiator = DfuServiceInitiator(mac)
            .setDeviceName(MmkvUtils.getConnDeviceName())
            .setKeepBond(false)
            .setForceDfu(false)
            .setPacketsReceiptNotificationsEnabled(true)
            .setPacketsReceiptNotificationsValue(6)
            .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        dfuServiceInitiator.disableResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(this)
        }
        dfuServiceInitiator.setZip(uri)
        dfuServiceInitiator.start(this,DfuService::class.java)
    }


    override fun onDestroy() {
        super.onDestroy()
        //
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener)
    }

    private val mDfuProgressListener : DfuProgressListener = object : DfuProgressListener{
        override fun onDeviceConnecting(deviceAddress: String?) {
            Timber.e("------onDeviceConnecting--------")
        }

        override fun onDeviceConnected(deviceAddress: String?) {
            Timber.e("-------onDeviceConnected-------")
        }

        override fun onDfuProcessStarting(deviceAddress: String?) {
            Timber.e("------onDfuProcessStarting--------")
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
            Timber.e("------onProgressChanged--------="+percent)
        }

        override fun onFirmwareValidating(deviceAddress: String?) {
            Timber.e("-----onFirmwareValidating---------")
        }

        override fun onDeviceDisconnecting(deviceAddress: String?) {
            Timber.e("-----onDeviceDisconnecting---------")
        }

        override fun onDeviceDisconnected(deviceAddress: String?) {
            Timber.e("----onDeviceDisconnected----------")
        }

        override fun onDfuCompleted(deviceAddress: String?) {
            Timber.e("-------onDfuCompleted-------")
        }

        override fun onDfuAborted(deviceAddress: String?) {
            Timber.e("------onDfuAborted--------")
        }

        override fun onError(deviceAddress: String?, error: Int, errorType: Int, message: String?) {
            Timber.e("--------onError------="+error+" "+message)
        }

    }
}