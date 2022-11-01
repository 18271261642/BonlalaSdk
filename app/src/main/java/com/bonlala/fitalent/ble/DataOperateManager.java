package com.bonlala.fitalent.ble;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.blala.blalable.BleConstant;
import com.blala.blalable.BleOperateManager;
import com.blala.blalable.Utils;
import com.blala.blalable.listener.OnCommBackDataListener;
import com.blala.blalable.listener.OnExerciseDataListener;
import com.blala.blalable.listener.OnMeasureDataListener;
import com.blala.blalable.listener.WriteBack24HourDataListener;
import com.blala.blalable.listener.WriteBackDataListener;
import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.db.DBManager;
import com.bonlala.fitalent.db.model.DeviceSetModel;
import com.bonlala.fitalent.db.model.ExerciseModel;
import com.bonlala.fitalent.emu.ConnStatus;
import com.bonlala.fitalent.emu.DbType;
import com.bonlala.fitalent.emu.SleepType;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.fitalent.utils.MmkvUtils;
import com.google.gson.Gson;
import com.haibin.calendarview.CalendarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * 数据的操作类，数据串联获取保存在此进行
 * Created by Admin
 * Date 2022/9/22
 * @author Admin
 */
public class DataOperateManager {

    private static DataOperateManager dataOperateManager;
    private static Context mContext;

    private final String userId = "user_1001";
    /**手表返回有效天数的数据天**/
    private int deviceBackDay = 0;


    private int deviceValidDay = 0;

    public static DataOperateManager getInstance(Context context){
        mContext = context;
        synchronized (DataOperateManager.class){
            if(dataOperateManager == null){
                dataOperateManager = new DataOperateManager();
            }
        }

        return dataOperateManager;
    }

    private DataOperateManager() {
    }


    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                get24HourData(BleOperateManager.getInstance(),deviceValidDay);
            }

        }
    };


    //获取mac
    private String getMac(){
        String mac = MmkvUtils.getConnDeviceMac();
        return mac;
    }

    //获取blename
    private String getBleName()
    {
        return MmkvUtils.getConnDeviceName();
    }


    /**监听锻炼数据**/
    public void setExerciseListener(BleOperateManager bleOperateManager){
        sb.delete(0,sb.length());
        exArrayList.clear();
        bleOperateManager.setExerciseDataListener(new OnExerciseDataListener() {
            @Override
            public void backExerciseData(byte[] data) {
                Timber.e("-------锻炼数据="+Utils.formatBtArrayToString(data));

                dealWithExercise(data);
            }
        });
    }

    private final StringBuilder sb = new StringBuilder();

    private final List<byte[]> exArrayList = new ArrayList<>();
    /**处理锻炼数据**/
    public void dealWithExercise(byte[] data){
        String str = null;
        //结束了
        if((data[0] & 0xff) == 129){
            if(sb.length() == 0)
                return;
            byte[] exerciseArray = Utils.hexStringToByte(sb.toString());
            ExerciseModel exerciseModel = new ExerciseModel();
            //运动类型
            int type = exerciseArray[1] & 0xff;
            exerciseModel.setType(type);
            //平均心率
            int avgHr = exerciseArray[2] & 0xff;
            exerciseModel.setAvgHr(avgHr);
            //开始年
            int year = (exerciseArray[3] & 0xff) + 2000;
            //月
            int month = exerciseArray[4] & 0xff;
            //日
            int day = exerciseArray[5] & 0xff;
            //时
            int hour = exerciseArray[6] & 0xff;
            //分
            int minute = exerciseArray[7] & 0xff;
            //秒
            int second = exerciseArray[8] & 0xff;

            if(month==0 || month>12 || day>31)
                return;


            String yearStr = year+"-"+String.format("%02d",month)+"-"+String.format("%02d",day);
            String startTime = yearStr+" "+String.format("%02d",hour)+":"+String.format("%02d",minute)+":"+String.format("%02d",second);
            exerciseModel.setStartTime(startTime);
            exerciseModel.setDayStr(yearStr);

            //运动总时间
            //时
            int hourTime = exerciseArray[9] & 0xff;
            //分钟
            int minuteTime = exerciseArray[10] & 0xff;
            //秒
            int secondTime = exerciseArray[11] & 0xff;
            exerciseModel.setSportHour(hourTime);
            exerciseModel.setSportMinute(minuteTime);
            exerciseModel.setSportSecond(secondTime);


            //结束年
            int endYear = (exerciseArray[12] & 0xff) + 2000;
            int endMonth = exerciseArray[13] & 0xff;
            int endDay = exerciseArray[14] & 0xff;
            int endHour = exerciseArray[15] & 0xff;
            int endMinute = exerciseArray[16] & 0xff;
            int endSecond = exerciseArray[17] & 0xff;



            String endYearStr = endYear+"-"+String.format("%02d",endMonth)+"-"+String.format("%02d",endDay);
            String endTime = endYearStr+" "+String.format("%02d",endHour)+":"+String.format("%02d",endMinute)+":"+String.format("%02d",endSecond);
            exerciseModel.setEndTime(endTime);

            //运动总步数
            int step = Utils.getIntFromBytes((byte) 0x00,exerciseArray[20],exerciseArray[19],exerciseArray[18]);
            //运动平均速度
            int avgSpeed = Utils.getIntFromBytes(exerciseArray[24],exerciseArray[23],exerciseArray[22],exerciseArray[21]);

            //总距离
            int countDistance = Utils.getIntFromBytes(exerciseArray[28],exerciseArray[27],exerciseArray[26],exerciseArray[25]);

            //卡路里
            int kcal =  Utils.getIntFromBytes(exerciseArray[32],exerciseArray[31],exerciseArray[30],exerciseArray[29]);

           exerciseModel.setCountStep(step);
           exerciseModel.setAvgSpeed(avgSpeed);
           exerciseModel.setDistance(countDistance);
           exerciseModel.setKcal(kcal);

           StringBuilder eSb = new StringBuilder();

           Timber.e("-----exArrayList="+new Gson().toJson(exArrayList));
          if(exArrayList.size()>2){
              for(int i = 2;i<exArrayList.size();i++){
                  byte[] arr = exArrayList.get(i);
                  byte[] tmpArr = new byte[19];
                  System.arraycopy(arr,1,tmpArr,0,19);
                  eSb.append(Utils.getHexString(tmpArr));
              }
          }

            byte[] lastPackBye = new byte[19];
            System.arraycopy(data,1,lastPackBye,0,19);
            eSb.append(Utils.getHexString(lastPackBye));

            Timber.e("------est="+eSb.toString());


            byte[] resultHr = Utils.hexStringToByte(eSb.toString());

            /**
             * 处理心率数据
             * 第一包是结尾包
             */
            List<Integer> hrList = new ArrayList<>();
            for(int i = 1;i<resultHr.length;i++){
                int hr = resultHr[i] & 0xff;
                if(hr == 255)
                    continue;
                if (hr <= 30) {
                    continue;
                }
                hrList.add(hr);
            }


            exerciseModel.setHrArray(new Gson().toJson(hrList));

            Timber.e("-----锻炼="+exerciseModel.toString());

            DBManager.dbManager.saveExerciseData("user_1001",MmkvUtils.getConnDeviceMac(),startTime,exerciseModel);
            BleOperateManager.getInstance().setClearListener();
            BaseApplication.getInstance().setConnStatus(ConnStatus.CONNECTED);

            setCommBroad(BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION);
        }else{
            exArrayList.add(data);
            String tmp = Utils.getHexString(data);
            sb.append(tmp);
        }


    }



    /**监听测量的数据，然后保存数据库**/
    public void setMeasureDataSave(BleOperateManager bleOperateManager){

        bleOperateManager.setMeasureDataListner(new OnMeasureDataListener() {
            @Override
            public void onMeasureHeart(int heart, long time) {

                DBManager.dbManager.saveMeasureHeartData(userId,getMac(),time,heart);
            }

            @Override
            public void onMeasureBp(int sBp, int disBp, long time) {
                Timber.e("----测量血压--");
                DBManager.dbManager.saveMeasureBp(userId,getMac(),time,sBp,disBp);
            }

            @Override
            public void onMeasureSpo2(int spo2, long time) {
                DBManager.dbManager.saveMeasureSpo2(userId,getMac(),time,spo2);
            }
        });
    }


    private DeviceSetModel deviceSetModel = null;

    //连接成功获取设置项
    public void readAllDataSet(BleOperateManager bleOperateManager){
        deviceSetModel = new DeviceSetModel();
        deviceSetModel.assignBaseObjId(0);
        BaseApplication.getInstance().setConnStatus(ConnStatus.IS_SYNC_DATA);
        bleOperateManager.readBattery(new OnCommBackDataListener() {
            @Override
            public void onIntDataBack(int[] value) {
                if(deviceSetModel != null){
                    deviceSetModel.setBattery(value[0]);
                }
                readDeviceInfo(bleOperateManager);

            }

            @Override
            public void onStrDataBack(String... value) {

            }
        });
    }

    private void readDeviceInfo(BleOperateManager bleOperateManager){
        bleOperateManager.getDeviceVersionData(new OnCommBackDataListener() {
            @Override
            public void onIntDataBack(int[] value) {

            }

            @Override
            public void onStrDataBack(String... value) {
                //有多少天的有效24小时数据
                int validDataDay = Integer.parseInt(value[2]);
                Timber.e("--------几天24小时的数据="+validDataDay);
                deviceBackDay = validDataDay;
                //固件版本号
                String hardVersion = value[0];
                //版本
                String versionStr = value[1];
                if(deviceSetModel != null){
                    deviceSetModel.setDeviceVersionCode(Integer.parseInt(hardVersion));
                    deviceSetModel.setDeviceVersionName(versionStr);
                }

                readStepGoal(bleOperateManager);
            }
        });
    }

    //读取计步目标
    private void readStepGoal(BleOperateManager bleOperateManager){
        bleOperateManager.readStepTarget(new OnCommBackDataListener() {
            @Override
            public void onIntDataBack(int[] value) {
                if(deviceSetModel != null){
                    deviceSetModel.setStepGoal(value[0]);
                }
                MmkvUtils.saveStepGoal(value[0]);
                readCommSet(bleOperateManager);
            }

            @Override
            public void onStrDataBack(String... value) {

            }
        });
    }

    //读取通用设置
    private void readCommSet(BleOperateManager bleOperateManager){
        bleOperateManager.getCommonSetting(new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Timber.e("-----通用在短短的="+Utils.formatBtArrayToString(data));
                if((data[0] & 0xff) == 0x0A  && (data[1] & 0xff) == 0xff){
                    byte valueByte = data[3];
                    byte[] valueArray = Utils.byteToBitOfArray(valueByte);
                   // Timber.e("-----通用设置="+Utils.formatBtArrayToString(valueArray));
                    //[0, 0, 1, 0, 0, 0, 0, 0]
                    //24小时心率
                    deviceSetModel.setIs24Heart(valueArray[3]==0);
                    //稳定单位，0摄氏度
                    deviceSetModel.setTempStyle(valueArray[2]& 0xff);
                    //12小时制
                    deviceSetModel.setTimeStyle(valueArray[5] & 0xff);
                    //中英文
                    //deviceSetModel?.set
                    //公英制 0公制
                    deviceSetModel.setIsKmUnit(valueArray[7] &0xff);
                    MmkvUtils.saveUnit((valueArray[7] & 0xff) == 0);

                }

                readLightData(bleOperateManager);
            }
        });
    }

    /**读取亮屏时间和亮度等级**/
    private void readLightData(BleOperateManager bleOperateManager){
        bleOperateManager.getBackLight(new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data[0] == 3 && (data[1] & 0xff) == 0xff){
                    //背光等级 1~3
                    int backLight = data[3]& 0xff;
                    int lightTime = data[4] & 0xff;
                    deviceSetModel.setLightLevel(backLight);
                    deviceSetModel.setLightTime(lightTime);



                    saveDeviceSetDatToDb(deviceSetModel,bleOperateManager);
                }
            }
        });
    }

    /**保存设置数据到数据库**/
    private void saveDeviceSetDatToDb(DeviceSetModel dm,BleOperateManager bleOperateManager){
        if(dm == null)
            return;
        String saveMac = MmkvUtils.getConnDeviceMac();
        if(BikeUtils.isEmpty(saveMac))
            return;
        boolean isSuccess = DBManager.getInstance().saveDeviceSetData("user_1001",saveMac,dm);
        Timber.e("----设置保存状态="+isSuccess+" "+saveMac+" ");

        setCommBroad(BleConstant.BLE_SYNC_COMPLETE_SET_ACTION);


        /**
         * 判断数据库中保存的最近一次的数据，
         */
        String lastHrDay = DBManager.dbManager.getLastDayOfType("user_1001",getMac(), DbType.DB_TYPE_DETAIL_HR);

        int dayInterval = BikeUtils.dayIntervalOfDay(lastHrDay == null ? BikeUtils.getBeforeDayStr(deviceBackDay) : lastHrDay,BikeUtils.getCurrDate());
        if(dayInterval == 0){
            deviceBackDay = 0;
        }


        get24HourData(bleOperateManager,deviceValidDay);
    }

    //读取汇总的计步数据
    public void getOneDayData(BleOperateManager bleOperateManager,int day){
        bleOperateManager.getCountDayData(day, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Timber.e("-------天数汇总的数据="+Utils.formatBtArrayToString(data));
            }
        });
    }


    private final StringBuffer stringBuffer = new StringBuffer();
    private int[] stepArray = new int[1440];

    //计步集合，一天最多1440个，不够补0
    private List<Integer> stepList = new ArrayList<>();
    //睡眠集合 晚上8点到第二天的8点，这里处理24:00~08：00总共8 *60 = 480个点
    private List<Integer> sleepList = new ArrayList<>();
    //心率，一天最多1440个，不够补0
    private List<Integer> heartList = new ArrayList<>();

    /**睡眠，一整天的睡眠分为两段，0点到8点为一段；20:00到24:00为一段**/
    private List<Integer> morningSleep = new ArrayList<>();
    private List<Integer> nightSleep = new ArrayList<>();


    //日期，
    int yearStr = 0;
    int monthStr = 0;
    int dayStr = 0;

    int dayStep = 0;
    int dayCalories = 0;
    int dayDistance = 0;

    private void init(){
        stringBuffer.delete(0,stringBuffer.length());
        stepList.clear();
        sleepList.clear();
        heartList.clear();
        morningSleep.clear();
        nightSleep.clear();

        yearStr = 0;
        monthStr = 0;
        dayStr = 0;

    }

    /**获取24小时的数据 0 今天；1昨天**/
    public void get24HourData(BleOperateManager bleOperateManager,int day){
        Timber.e("-------获取24小时数据=="+day);
        init();
        bleOperateManager.setClearListener();
        bleOperateManager.setClearExercisListener();
        bleOperateManager.getDay24HourForData(day, new WriteBack24HourDataListener() {
            @Override
            public void onWriteBack(byte[] data) {
                analysisData(data);
            }
        });
    }


    private void analysisData(byte[] data){
        //第一包
        if(data[0] == 0 &&  data[1] == -118){
            //年月日
            int year = Utils.getIntFromBytes(data[6],data[5]);
            int month = data[7] &0xff;
            int day = data[8] &0xff;
            //当天的总步数
            int countStep = Utils.getIntFromBytes((byte) 0x00,data[11],data[10],data[9]);
            //卡路里
            int countKcal = Utils.getIntFromBytes((byte) 0x00,data[14],data[13],data[12]);
            //距离
            int countDistance = Utils.getIntFromBytes((byte) 0x00,data[17],data[16],data[15]);

            this.yearStr = year;
            this.monthStr = month;
            this.dayStr = day;

            dayStep = countStep;
            dayCalories = countKcal;
            dayDistance = countDistance;

            Timber.e("-----24小时数据="+year+"-"+month+"-"+day +" "+countDistance+" "+countStep+" "+countKcal);
        }else{
            //去除第一个byte，剩下的添加到数组中
            byte[] tempArray = new byte[19];
            System.arraycopy(data,1,tempArray,0,19);

            Timber.e("-----arr="+ Arrays.toString(tempArray)+"\n"+((data[0])));
            //转换为字符串，全部拼接起来
            String tmpStr = Utils.getHexString(tempArray);

            //判断是否是最后一包

            //判断是否是最后一包，最后一包0x81开头,最后一个包，去掉首位，如果连续两个ff就截止
            if(data[0] == -127){
                byte[] lastPack = new byte[19];
                System.arraycopy(data,1,lastPack,0,19);

                for(int k = 0;k<lastPack.length;k++){
                    if(k+1<lastPack.length){
                        int value = lastPack[k] &0xff;
                        int value2 = lastPack[k+1] & 0xff;
                        //截止了
                        if(value == 255 && value2 == 255){
                            break;
                        }
                        stringBuffer.append(String.format("%02x",lastPack[k]));
                    }
                }


                //转为byte数组，解析
                int tempStep = 0;
                byte[] resultBtArray = Utils.hexStringToByte(stringBuffer.toString());
                Timber.e("---总长度="+resultBtArray.length);

                //睡眠状态 0深睡；1浅睡；2清醒
                int sleepStatus;

                for(int i = 0;i<resultBtArray.length;i+=2){
                    if(i+1<resultBtArray.length){
                        sleepStatus = SleepType.SLEEP_TYPE_AWAKE;
                        //第一个包 步数 BYTE0 - 该时间段步数，若>=250则为睡眠(250为深睡\251为浅睡\253为清醒\254为未佩戴)
                        int  itemStep = resultBtArray[i] & 0xff;
                        //第二个包 BYTE1 - 该时间段平均心率，若为0，表示没有开启心率功能，若为大于等于1，小于30，表示心率开启了，但是手表不在手腕上
                        int  itemHeart = resultBtArray[i+1] & 0xff;
                        //有睡眠时，计步是0
                        if(itemStep>=250){
                            tempStep = 0;
                            if(itemStep == 250){
                                sleepStatus = SleepType.SLEEP_TYPE_DEEP;
                            }
                            if(itemStep == 251){
                                sleepStatus = SleepType.SLEEP_TYPE_LIGHT;
                            }
                            if(itemStep == 253){
                                sleepStatus = SleepType.SLEEP_TYPE_AWAKE;
                            }

                        }else{
                            tempStep = itemStep;
                        }

                        if (i / 2 < 480 && morningSleep.size() < 480) {
                            morningSleep.add(sleepStatus);
                        }

                        if (i / 2 >= 1200 && nightSleep.size() < 240) {
                            nightSleep.add(sleepStatus);
                        }

                        heartList.add(itemHeart == 255 || itemHeart == 254 ? 0 : itemHeart);
                        stepList.add(tempStep);
                    }
                }

                Timber.e("-------结果="+stepList.size()+"   "+sleepList.size()+"  "+heartList.size());
                Timber.e("-------睡眠="+new Gson().toJson(morningSleep)+" "+morningSleep.size());
                //日期
                String dateStr = yearStr+"-"+String.format("%02d",monthStr)+"-"+String.format("%02d",dayStr);
                //存储计步
                DBManager.dbManager.saveCurrentDayDetailStep("user_1001",getBleName(),getMac(),dateStr,dayStep,dayCalories,dayDistance,stepList);
               new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       //保存心率
                       DBManager.getInstance().saveOnDayHeartData("user_1001",getBleName(),getMac(),dateStr,heartList);
                   }
               },1000);
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //存储睡眠
                        DBManager.getInstance().saveOnDaySleepData("user_1001",getBleName(),getMac(),dateStr,morningSleep,nightSleep);
                    }
                },1000);

                BleOperateManager.getInstance().setClearListener();
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setCommBroad(BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION);
                    }
                },1000);


                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //deviceValidDay <=1 &&
                        if(deviceValidDay+1<deviceBackDay){
                            deviceValidDay++;
                            handler.sendEmptyMessageDelayed(0x00,500);
                        }else{
                            deviceValidDay=0;
                            getExerciseData();
                        }
                    }
                },1500);

            }else{
                stringBuffer.append(tmpStr);
            }
        }
    }

    /**获取锻炼数据**/
    public void getExerciseData(){
        BleOperateManager.getInstance().setClearListener();
        DataOperateManager.getInstance(mContext).setExerciseListener(BleOperateManager.getInstance());
        BleOperateManager.getInstance().getExerciseData(0, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Timber.e("----获取锻炼返回="+Utils.formatBtArrayToString(data));
                if(data[0] == 2 && (data[1] & 0xff) == 0xff && (data[2] &0xff) == 66 && data[3]==4){
                    BleOperateManager.getInstance().setClearListener();
                    BaseApplication.getInstance().setConnStatus(ConnStatus.CONNECTED);

                    setCommBroad(BleConstant.BLE_24HOUR_SYNC_COMPLETE_ACTION);
                }
            }
        });

    }


    private void setCommBroad(String action){
        Intent intent = new Intent();
        intent.setAction(action);
        if(mContext == null)
            return;
        mContext.sendBroadcast(intent);
    }
}
