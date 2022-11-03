package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.SleepItem
import com.bonlala.fitalent.db.model.SleepModel
import com.bonlala.fitalent.emu.DbType
import com.bonlala.fitalent.emu.SleepType
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.GsonUtils
import com.google.gson.Gson
import timber.log.Timber
import java.util.*


/**
 * 睡眠的viewModel
 * Created by Admin
 *Date 2022/10/18
 */
open class HistorySleepViewModel : ViewModel() {

    //睡眠数据
    var onDaySleep = MutableLiveData<SleepModel?>()

    //记录的睡眠日期
    var recordSleep = MutableLiveData<List<String>>()

    //最近一次的睡眠数据
    val lastRecordSleep = MutableLiveData<SleepModel?>()



    //获取睡眠的记录
    fun getSleepRecord(mac: String){
        val sleepRecord = DBManager.getInstance().getAllRecordByType("user_1001",mac, DbType.DB_TYPE_SLEEP)
        recordSleep.postValue(sleepRecord)
    }

    /**
     * 获取睡眠数据
     */
    fun getSleepForLastDay(mac : String) {

        var sleepRecord = DBManager.dbManager.getLastDayOfType("user_1001",mac,DbType.DB_TYPE_SLEEP)
        if(sleepRecord == null)
            sleepRecord = BikeUtils.getCurrDate()

        val sleepB = DBManager.dbManager.getDaySleepData("user_1001",mac,sleepRecord)

        //Timber.e("----ss="+Gson().toJson(sleepB))
        //再查询上一天的
        val sleepPreviewB = DBManager.dbManager.getDaySleepData("user_1001",mac,BikeUtils.getBeforeOrAfterDayStr(sleepRecord,true))
       // Timber.e("----222ss="+Gson().toJson(sleepPreviewB))
        //上午的str
        var morningStr : String ?= null
        //晚上的str
        var nightStr : String ?= null

        if(sleepB != null){
            //上午的str
            morningStr = sleepB.morningSleepStr
        }

        if(sleepPreviewB != null){
            nightStr = sleepPreviewB.nightSleepStr
        }
        val morList = morningStr?.let { GsonUtils.getGsonObject<List<Int>>(it) }
        val nigList = nightStr?.let { GsonUtils.getGsonObject<List<Int>>(it) }
//        Timber.e("------今天上午的睡眠="+morList?.size+" "+morningStr)
//        Timber.e("-----昨天晚上的睡眠="+nigList?.size+" "+nightStr)

        if(morningStr == null && nightStr == null){
            lastRecordSleep.postValue(null)
            return
        }

        if(morningStr == null)
            morningStr = dealWithMorningData()
        if(nightStr == null)
            nightStr = dealWithNightData()

        val result = mergeSleepData(morningStr,nightStr,mac,sleepRecord)

        lastRecordSleep.postValue(result)
    }


    /**
     * 获取睡眠数据
     */
    fun getSleepForDay(mac : String,day : String){
        val sleepB = DBManager.dbManager.getDaySleepData("user_1001",mac,day)

        Timber.e("----ss="+Gson().toJson(sleepB))
        //再查询上一天的
        val sleepPreviewB = DBManager.dbManager.getDaySleepData("user_1001",mac,BikeUtils.getBeforeOrAfterDayStr(day,true))
        //Timber.e("----222ss="+Gson().toJson(sleepPreviewB))
        //上午的str
        var morningStr : String ?= null
        //晚上的str
        var nightStr : String ?= null

        if(sleepB != null){
            //上午的str
           morningStr = sleepB.morningSleepStr
        }

        if(sleepPreviewB != null){
            nightStr = sleepPreviewB.nightSleepStr
        }
        val morList = morningStr?.let { GsonUtils.getGsonObject<List<Int>>(it) }
        val nigList = nightStr?.let { GsonUtils.getGsonObject<List<Int>>(it) }
//        Timber.e("------今天上午的睡眠="+morList?.size+" "+morningStr)
//        Timber.e("-----昨天晚上的睡眠="+nigList?.size+" "+nightStr)

        if(morningStr == null && nightStr == null){
            onDaySleep.postValue(null)
            return
        }


        if(morningStr == null)
            morningStr = dealWithMorningData()
        if(nightStr == null)
            nightStr = dealWithNightData()

        val result = mergeSleepData(morningStr,nightStr,mac,day)

        onDaySleep.postValue(result)
    }


    private fun mergeSleepData(morningStr : String,nightStr : String,mac: String,day: String) : SleepModel{
        val listMorning = GsonUtils.getGsonObject<List<Int>>(morningStr)

        val listNight = GsonUtils.getGsonObject<List<Int>>(nightStr)

        Timber.e("----lmo="+listMorning?.size+" "+listNight?.size)

        //总的睡眠，一整天的
        val allSleepList = mutableListOf<Int>()

        if(listNight != null){
            allSleepList.addAll(listNight)
        }
        if (listMorning != null) {
            allSleepList.addAll(listMorning)
        }

        if(Collections.max(allSleepList)==2 && Collections.min(allSleepList)==2){
            return SleepModel()
        }

        val sleepModel = SleepModel()
        sleepModel.deviceMac= mac
        sleepModel.saveDay = day

        //当天的
        val dayCalendar = BikeUtils.getDayCalendar(day)
        //上一天的
        val previewDayCalendar = BikeUtils.getDayCalendar(BikeUtils.getBeforeOrAfterDayStr(day,true))

        //处理睡眠

        previewDayCalendar.set(Calendar.HOUR_OF_DAY,20)
        previewDayCalendar.set(Calendar.MINUTE,0)

        dayCalendar.set(Calendar.HOUR_OF_DAY,0)

        //Timber.e("----all="+Gson().toJson(allSleepList))
        //开始时间
        var startTime = 0

        for(i in 0 until allSleepList.size){
            if(allSleepList[i] !=2){
                startTime = i
                break
            }
        }

        Timber.e("---startTime="+startTime+" "+allSleepList.size)
        //开始时间
        val resultStartTime = 20 * 60 + startTime

        //结束的下标
        var endTime = 0


        for(i in allSleepList.size-1 downTo  0){
          //  Timber.e("------aaaa="+allSleepList[i]+" "+i)
            if(allSleepList[i] != 2){
                endTime = i
                break
            }
        }


        val resultEndTime = 8 * 60  - (allSleepList.size-endTime)

      //  Timber.e("---endTime="+endTime+" ==="+resultEndTime+" "+resultStartTime+"\n")

        val resultSleepList = mutableListOf<Int>()

        //深睡
        var deepTime = 0
        //浅睡
        var lightTime = 0
        //清醒
        var awakeTime = 0
        allSleepList.forEachIndexed { index, i ->
            if(index in startTime .. endTime){
                resultSleepList.add(i)
            }
        }

        val sleepSource = getTest(resultSleepList,resultStartTime)
       // Timber.e("--stt="+Gson().toJson(sleepSource)+"\n"+resultSleepList.size+" "+Gson().toJson(resultSleepList))

        var resultAwakeTime = 0
        sleepSource?.forEach {
            if(it.sleepType == SleepType.SLEEP_TYPE_AWAKE){
                resultAwakeTime +=it.sleepLength
            }
            if(it.sleepType == SleepType.SLEEP_TYPE_DEEP){
                deepTime +=it.sleepLength
            }
            if(it.sleepType == SleepType.SLEEP_TYPE_LIGHT){
                lightTime +=it.sleepLength
            }
        }

        //Timber.e("----清醒时间="+resultAwakeTime+" "+deepTime+" "+lightTime)

        sleepSource?.get(sleepSource.size -1)?.isClick = true
        sleepModel.lightTime = lightTime
        sleepModel.deepTime = deepTime
        sleepModel.awakeTime = resultAwakeTime
        sleepModel.sleepSource = Gson().toJson(sleepSource)
        sleepModel.fallAsleepTime = resultStartTime
        sleepModel.countSleepTime = resultSleepList.size
        sleepModel.wakeUpTime = resultEndTime

        return sleepModel

    }



    //组装第二天的数据 8 * 60个0
    private fun dealWithMorningData() : String{
        val list = mutableListOf<Int>()
        for(i in 0 until 480){
            list.add(2)
        }
        return Gson().toJson(list)
    }

    private fun dealWithNightData() : String{
        val list = mutableListOf<Int>()
        for(i in 0 until 240){
            list.add(2)
        }
        return Gson().toJson(list)
    }


    private fun getTest(array : List<Int>,startTime : Int): List<SleepItem>? {
        //List<Integer> mOriginal = new ArrayList<>();
       // Timber.e("-----ssssssss="+Gson().toJson(array))
        val list = ArrayList<SleepItem>()
        var tempStartTime = startTime
        var n = 0
        for (i in 0 until array.size - 1) {
            val j = n
            var length = 0
            //println("第一个循环：j$j")
            if (n < array.size) {
                for (k in j until array.size) {
                    if (array[j] == array[k]) {
                        length++
                      // println("累加中：j：" + j + "n:" + n + "k:" + k + "length:" + length)
                    } else {
                        // mLength.add(length);
                       // list.add(SleepItem(length, array[j]))
                        val startT = tempStartTime
                        val endT = tempStartTime+length

                        n = k
                      // println("添加完：length:" + length + "k:" + k)
                       // Timber.e("--111开始时间="+startT+" 长度="+length+" 结束时间="+endT+" 类型="+array[k-1])
                        list.add(SleepItem(startT,length,endT,array[k-1]))

                        tempStartTime+=length
                        break
                    }
                    if (k == array.size - 1) {
                        val startT = tempStartTime
                        val endT = tempStartTime+length
                       // Timber.e("--22开始时间="+startT+" 长度="+length+" 结束时间="+endT+" 类型="+array[k-1])
                        // mLength.add(length);
                      //  list.add(SleepItem(length, array[k]))
                        list.add(SleepItem(startT,length,endT,array[k-1]))
                        n = array.size

                        tempStartTime+=length
                        break
                    }
                }
            }
        }
        return list
    }
}