package com.bonlala.fitalent.ui.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blala.blalable.BleOperateManager
import com.blala.blalable.Utils
import com.blala.blalable.blebean.CommBleSetBean
import com.blala.blalable.listener.OnCommBackDataListener
import com.blala.blalable.listener.WriteBack24HourDataListener
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.fitalent.R
import com.bonlala.fitalent.ble.DataOperateManager
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DeviceSetModel
import com.bonlala.fitalent.emu.DeviceNotifyType
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.VersionApi
import com.bonlala.fitalent.utils.GsonUtils
import com.google.gson.Gson
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*
import kotlin.experimental.and

class DashboardViewModel : ViewModel() {



    var commSetModel = MutableLiveData<DeviceSetModel>()
    var deviceSetModel : DeviceSetModel?=null

    //电量
     var batteryStr = MutableLiveData<String>()

     val byteArray = MutableLiveData<ByteArray>()

    //获取是否有固件新版本
    var serverVersion = MutableLiveData<String>()

    //获取转腕亮屏、久坐提醒、勿扰模式的信息，数据库中获取
    var deviceNotifyData = MutableLiveData<MutableList<String>>()



    //设置亮度等级和时长
    fun setLightAndInterval(bleOperateManager: BleOperateManager,light : Int,level : Int){
        bleOperateManager.setBackLight(light, level,writeBackDataListener)
    }

    //温度单位
    fun setCommSet(bleOperateManager: BleOperateManager,commBleSetBean: CommBleSetBean){
        bleOperateManager.setCommonSetting(commBleSetBean,writeBackDataListener)
    }


    /**
     * 查询勿扰、久坐、抬腕提醒
     */
    fun getDeviceNotifyData(userId: String,mac: String,context: Context){

        val list = mutableListOf<String>()
        //勿扰
        val dnt = DBManager.getInstance().getDbNotifyType(userId,mac,DeviceNotifyType.DB_DNT_TYPE);
        var dntStr :String?=null
        if(dnt != null){
            dntStr = dnt.getStartAndEndTime(context)
        }else{
            dntStr = " ";
        }
        if (dntStr != null) {
            list.add(dntStr)
        }
        //久坐
        var sedentaryStr : String ?= null
        val sedentary = DBManager.getInstance().getDbNotifyType(userId,mac,DeviceNotifyType.DB_LONG_DOWN_SIT_TYPE)
        if(sedentary !=null){
            sedentaryStr = sedentary.getStartAndEndTime(context)
        }else{
            sedentaryStr = " "
        }

        if (sedentaryStr != null) {
            list.add(sedentaryStr)
        }

        //转腕
        var raiseStr : String ?= null
        var raise = DBManager.getInstance().getDbNotifyType(userId,mac,DeviceNotifyType.DB_RAISE_TO_WAKE)
        if(raise == null){
            raiseStr = " ";
        }else{
            raiseStr = raise.getStartAndEndTime(context)
        }

        if (raiseStr != null) {
            list.add(raiseStr)
        }

        Timber.e("------list="+Gson().toJson(list))
        deviceNotifyData.postValue(list)

    }





    //获取电量
    fun getDeviceBattery(bleOperateManager: BleOperateManager){
        bleOperateManager.readBattery(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
                batteryStr.postValue("电量:"+ (value?.get(0) ?: "--"))
            }

            override fun onStrDataBack(vararg value: String?) {

            }

        })
    }


    //后台获取固件版本信息
    fun getServerVersionInfo(lifecycle: LifecycleOwner){
        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM
        EasyConfig.getInstance().setServer(requestServer).into()
        EasyHttp.get(lifecycle).api(VersionApi().setVersion("W560B",1)).request(object :
            OnHttpListener<String> {
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.get("code") == "200" && jsonObject.get("success") == true){
                    val dataStr = jsonObject.getString("data")
                    val verBean = GsonUtils.getGsonObject<VersionApi.VersionInfo>(dataStr)

//                    Timber.e("----固件版本="+verBean.toString())
                    serverVersion.postValue(verBean?.versionName)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })

    }



    //设置实时心率开关
    fun setRealHeartStatus(bleOperateManager: BleOperateManager,isOpen : Boolean){
        bleOperateManager.setHeartStatus(isOpen){

        }
    }

    //设置计步目标
    fun setDeviceTargetStep(bleOperateManager: BleOperateManager,step : Int){
        bleOperateManager.setStepTarget(step){

        }
    }

    //连接成功后读取全部的设置
    fun readAllSetData(bleOperateManager: BleOperateManager,userId : String,mac : String){
        deviceSetModel = DeviceSetModel()

        deviceSetModel?.userId = userId;
        deviceSetModel?.deviceMac = mac
        Timber.e("-----读取素有设置="+(deviceSetModel == null))


        //读取电量
        bleOperateManager.readBattery(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
                deviceSetModel?.battery = value?.get(0)?.toInt() ?: 0

                readDeviceInfo(bleOperateManager,userId,mac)
            }

            override fun onStrDataBack(vararg value: String?) {

            }

        })
    }

    //读取固件信息
    fun readDeviceInfo(bleOperateManager: BleOperateManager,userId: String,mac: String){
        bleOperateManager.getDeviceVersionData(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {

            }

            override fun onStrDataBack(vararg value: String?) {
               //固件版本号
                val hardVersion = value.get(0)
                //版本
                val versionStr = value.get(1)
                Log.e("固件","---vvv="+hardVersion+" "+versionStr)
                if (hardVersion != null) {
                    deviceSetModel?.deviceVersionCode = hardVersion.toInt()
                    deviceSetModel?.deviceVersionName = versionStr
                }
                readStepGoal(bleOperateManager,userId,mac)
            }

        })
    }


    fun readStepGoal(bleOperateManager: BleOperateManager,userId: String,mac: String){
        //读取计步目标
        bleOperateManager.readStepTarget(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
                if (value != null) {
                    deviceSetModel?.stepGoal = value.get(0)
                }
                readCommSetData(bleOperateManager,userId,mac)
            }

            override fun onStrDataBack(vararg value: String?) {

            }
        })
    }

    //读取通用设置
    fun readCommSetData(bleOperateManager: BleOperateManager,userId: String,mac: String){
        bleOperateManager.getCommonSetting {
            Log.e("BB","---通用设置="+Utils.formatBtArrayToString(it))
            //0aff04200100000000000000
            if(it[0] and 0xff.toByte() == 0x0a.toByte() && it[1] and 0xff.toByte() == 0xff.toByte()){
                val valueByte = it[3]
                val valueArray = Utils.byteToBitOfArray(valueByte)
                //[0, 0, 1, 0, 0, 0, 0, 0]
                Log.e("BB","--22-通用设置="+Arrays.toString(valueArray))
                //24小时心率
                deviceSetModel?.isIs24Heart = valueArray[3].toInt() == 3
                //稳定单位，0摄氏度
                deviceSetModel?.tempStyle = valueArray[2].toInt()
                //12小时制
                deviceSetModel?.timeStyle = valueArray[5].toInt()
                //中英文
                //deviceSetModel?.set
                //公英制
                deviceSetModel?.isKmUnit = valueArray[7].toInt()

                readLightLevel(bleOperateManager,userId,mac)
            }
        }
    }

    //亮屏时间和亮度等级
    private fun readLightLevel(bleOperateManager: BleOperateManager,userId: String,mac: String){
        bleOperateManager.getBackLight {
            if(it[0].toInt() == 3 && it[1] and 0xff.toByte() == 0xff.toByte()){
                //背光等级 1~3
                val backLight = it[3].toInt()
                val lightTime = it[4].toInt()
                deviceSetModel?.lightLevel = backLight
                deviceSetModel?.lightTime = lightTime

               Timber.e("-------deviceSet="+Gson().toJson(deviceSetModel))
                //保存到数据库
                if(deviceSetModel == null)
                   return@getBackLight
                commSetModel.postValue(deviceSetModel)
                val isSucc = DBManager.getInstance().saveDeviceSetData(userId,mac,deviceSetModel)
                Log.e("DB","------是否保存成功="+isSucc)

            }
        }
    }




    private val stringBuffer = StringBuffer()
    //获取当日运动数据
    fun getCurrentDaySport(context: Context,bleOperateManager: BleOperateManager){
        stringBuffer.delete(0,stringBuffer.length)
        bleOperateManager.setClearExercisListener()
        DataOperateManager.getInstance(context).get24HourData(bleOperateManager,0)



    }
    private val writeBackDataListener = WriteBackDataListener { }

}