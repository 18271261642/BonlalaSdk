package com.bonlala.fitalent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blala.blalable.BleConstant
import com.blala.blalable.BleOperateManager
import com.blala.blalable.listener.BleConnStatusListener
import com.blala.blalable.listener.ConnStatusListener
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.activity.GuideActivity
import com.bonlala.fitalent.adapter.ScanDeviceAdapter
import com.bonlala.fitalent.bean.BleBean
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.BonlalaUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**搜索页面**/
class MainActivity : AppActivity() ,OnItemClickListener{


    private var scanDeviceAdapter : ScanDeviceAdapter ?= null
    private var listData : MutableList<BleBean> ?= null


    //用于去重的list
    private var repeatList : MutableList<String> ?= null

    private var handler = BleHandler()

    private class BleHandler() : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                BaseApplication.getInstance().bleOperate.stopScanDevice()
            }
        }
    }


    private fun verifyScanFun(){

        //判断蓝牙是否开启
        if(!BikeUtils.isBleEnable(this)){
            BikeUtils.openBletooth(this)
            return
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),0x01)
        }

        //判断权限
        val isPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if(!isPermission){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),0x00)
            return
        }

        //判断蓝牙是否打开
        val isOpenBle = BonlalaUtils.isOpenBlue(this@MainActivity)
        if(!isOpenBle){
            BonlalaUtils.openBluetooth(this)
            return
        }

        startScanDevice()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        startScanDevice()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        startScanDevice()
    }

    //搜索
    private fun startScanDevice(){
        listData?.clear()
        repeatList?.clear()
        startGif()
        BaseApplication.getInstance().bleOperate.scanBleDevice(object : SearchResponse{
            override fun onSearchStarted() {
                Log.e("sanc","-----开始扫描")
            }

            override fun onDeviceFounded(p0: SearchResult) {
                scanStatusTv.text = "扫描中.."

                val bleName = p0.name
                if(TextUtils.isEmpty(bleName) || bleName.equals("NULL"))
                    return
                if(!bleName.lowercase(Locale.ROOT).contains("w560b"))
                    return
                if(repeatList?.contains(p0.address) == true)
                    return
               repeatList?.add(p0.address)
                listData?.add(BleBean(p0.device,p0.rssi))
                listData?.sortBy {
                    Math.abs(it.rssi)
                }
                scanDeviceAdapter?.notifyDataSetChanged()

            }

            override fun onSearchStopped() {
                scanStatusTv.text = "扫描完成"
                stopGif()
            }

            override fun onSearchCanceled() {
                scanStatusTv.text = "扫描完成"

            }

        },1500 * 1000,1)
    }


    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.getInstance().bleOperate.stopScanDevice()
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_main
    }

    override fun initView() {

    }

    override fun initData() {
        repeatList = ArrayList<String>()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        scanRecyclerView.layoutManager = linearLayoutManager
//        scanRecyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        listData =ArrayList<BleBean>()
        scanDeviceAdapter = ScanDeviceAdapter(this,listData)
        scanRecyclerView.adapter = scanDeviceAdapter
        scanDeviceAdapter?.setOnItemClickListener(this)


        verifyScanFun()
    }

    override fun onIteClick(position: Int) {
        handler.sendEmptyMessage(0x00)

        val bleMac = listData?.get(position)?.bluetoothDevice?.address
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val bleName =  listData?.get(position)?.bluetoothDevice?.name

        showDialog("连接中..")
        BaseApplication.getInstance().connStatusService.connDeviceBack(bleName,bleMac
        ) { mac, status ->
            //连接成功
            if (status == 88) {
                BaseApplication.getInstance().connBleName = bleName
                BaseApplication.getInstance().connStatus = ConnStatus.CONNECTED
                MmkvUtils.saveConnDeviceName(bleName)
                MmkvUtils.saveConnDeviceMac(bleMac)

                val broadIntent = Intent()
                broadIntent.action = BleConstant.BLE_CONNECTED_ACTION
                sendBroadcast(broadIntent)
                //进入玩转设备页面
                startActivity(GuideActivity::class.java)

                finish()
            }
        }


//        BleOperateManager.getInstance().connYakDevice("b",bleMac,object : ConnStatusListener{
//            override fun connStatus(status: Int) {
//
//            }
//
//            override fun setNoticeStatus(code: Int) {
//                hideDialog()
//                Timber.e("----notifyCode="+code)
//                BaseApplication.getInstance().connBleName = bleName
//                BaseApplication.getInstance().connStatus = ConnStatus.CONNECTED
//                MmkvUtils.saveConnDeviceName(bleName)
//                MmkvUtils.saveConnDeviceMac(bleMac)
//
//                val broadIntent= Intent()
//                broadIntent.action = BleConstant.BLE_CONNECTED_ACTION
//                sendBroadcast(broadIntent)
//                //进入玩转设备页面
//                startActivity(GuideActivity::class.java)
//
//                finish()
//            }
//
//        })
    }



    //开始GIF
    private fun startGif(){
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        Glide.with(context).asGif().load(R.drawable.ic_scan_gif).apply(requestOptions)
            .into(scanImgView)
    }

    //停止gif
    private fun stopGif(){
        try {
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            Glide.with(context).asGif().load(R.drawable.ic_scan_gif).apply(requestOptions)
                .into(scanImgView)
            if(scanImgView.drawable is GifDrawable){

                val drawable = scanImgView.drawable as GifDrawable
                drawable.stop()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}