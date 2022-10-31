package com.bonlala.fitalent.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blala.blalable.BleOperateManager
import com.blala.blalable.Utils
import com.blala.blalable.blebean.CommBleSetBean
import com.blala.blalable.listener.OnCommBackDataListener
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.DeviceSetModel
import com.google.gson.Gson
import timber.log.Timber
import java.util.*
import kotlin.experimental.and

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text



    var commSetModel = MutableLiveData<DeviceSetModel>()
    var deviceSetModel : DeviceSetModel?=null

    //电量
     var batteryStr = MutableLiveData<String>()


     val byteArray = MutableLiveData<ByteArray>()



    //设置亮度等级和时长
    fun setLightAndInterval(bleOperateManager: BleOperateManager,light : Int,level : Int){
        bleOperateManager.setBackLight(light, level,writeBackDataListener)
    }

    //温度单位
    fun setCommSet(bleOperateManager: BleOperateManager,commBleSetBean: CommBleSetBean){
        bleOperateManager.setCommonSetting(commBleSetBean,writeBackDataListener)
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
    fun getCurrentDaySport(bleOperateManager: BleOperateManager){
        stringBuffer.delete(0,stringBuffer.length)
     /*   bleOperateManager.getDayForData(0) {
            Log.e("汇总数据","----="+Utils.formatBtArrayToString(it)+" "+(it[1].toInt()))

            val btArray  = it
            //如果是第一包，
            if(btArray[0].toInt() == 0 &&  btArray[1].toInt() == -118){
                //年月日
                val year = Utils.getIntFromBytes(btArray[6],btArray[5])
                val month = btArray[7].toInt()
                val day = btArray[8].toInt()
                //当天的总步数
                val countStep = Utils.getIntFromBytes(0x00,btArray[11],btArray[10],btArray[9])
                //卡路里
                val countKcal = Utils.getIntFromBytes(0x00,btArray[14],btArray[13],btArray[12])
                //距离
                val countDistance = Utils.getIntFromBytes(0x00,btArray[17],btArray[16],btArray[15])
                Log.e("步数","---步数="+year+" "+month+" "+day+" "+countStep+" "+countKcal+" "+countDistance)

            }else{
                //去除第一个byte，剩下的添加到数组中
                val tempArray = ByteArray(19)
                System.arraycopy(it,1,tempArray,0,19)

                Log.e("SSS","-----arr="+ Arrays.toString(tempArray)+"\n"+((btArray[0] and 0xff.toByte()).toInt()))
                //转换为字符串，全部拼接起来
                val tmpStr = Utils.getHexString(tempArray)
                stringBuffer.append(tmpStr)
                //判断是否是最后一包

                //判断是否是最后一包，最后一包0x81开头
                if((btArray[0] and 0xff.toByte()).toInt() == -127){   //最后一包
                    //转为byte数组，解析
                    val resultBtArray = Utils.hexStringToByte(stringBuffer.toString())
                        var i: Int = 0
                        while (i < resultBtArray.size) {
                            i += 2
                            Log.e("BBB","----i="+i)
                            if(i+1<resultBtArray.size){
                                //第一个包 步数 BYTE0 - 该时间段步数，若>=250则为睡眠(250为深睡\251为浅睡\253为清醒\254为未佩戴)
                                val itemStep = resultBtArray[i] and 0xFF.toByte()
                                //第二个包 BYTE1 - 该时间段平均心率，若为0，表示没有开启心率功能，若为大于等于1，小于30，表示心率开启了，但是手表不在手腕上
                                val itemHeart = resultBtArray[i+1] and 0xFF.toByte()

                                Log.e("结果","-----itemStep="+itemStep+" "+itemHeart)
                            }


                        }

                    bleOperateManager.setClearListener()
                    }

                }

            }*/


        }
    private val writeBackDataListener = WriteBackDataListener { }

}