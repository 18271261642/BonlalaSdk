package com.blala.blalable;


import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Admin
 * Date 2021/9/3
 * @author Admin
 */
public class BleConstant {


    /**连接成的广播action**/
    public static final String BLE_CONNECTED_ACTION = "com.blala.blalable.ble_connected";
    /**断开连接的action**/
    public static final String BLE_DIS_CONNECT_ACTION = "com.blala.blalable.ble_dis_connected";



    /**设置数据同步完成的广播**/
    public static final String BLE_SYNC_COMPLETE_SET_ACTION = "com.blala.blalable.set_complete";
    /**24小时数据同步完成**/
    public static final String BLE_24HOUR_SYNC_COMPLETE_ACTION = "com.blala.blalable.data_complete";
    /**手表结束锻炼，手表返回一个锻炼数据**/
    public static final String BLE_COMPLETE_EXERCISE_ACTION = "com.blala.blalable.exercise_finish";

    /**通用广播的action**/
    public static final String COMM_BROADCAST_ACTION = "comm_action";
    public static final String COMM_BROADCAST_KEY = "comm_action_key";

    /**测量完心率、血压、血氧的value**/
    public static final int MEASURE_COMPLETE_VALUE = -1;



    /**服务UUID**/
    public UUID SERVICE_UUID = UUID.fromString("7658fd00-878a-4350-a93e-da553e719ed0");

    /**命令发送特征uuid**/
    public UUID WRITE_UUID = UUID.fromString("7658fd01-878a-4350-a93e-da553e719ed0");
    /**实时返回数据到的UUID**/
    public UUID REAL_TIME_UUID = UUID.fromString("7658fd04-878a-4350-a93e-da553e719ed0");
    /**写入数据返回指令的UUID**/
    public UUID READ_UUID = UUID.fromString("7658fd02-878a-4350-a93e-da553e719ed0");
    /**存储数据返回**/
    public UUID SAVE_DATA_SEND_UUID = UUID.fromString("7658fd03-878a-4350-a93e-da553e719ed0");
    /**表盘接收**/
    public UUID WATCH_FACE_UUID = UUID.fromString("7658fd05-878a-4350-a93e-da553e719ed0");



    /**电量的serviceUUID，主动读取**/
    public UUID BATTERY_SERVER_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public UUID BATTERY_READ_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");


    public UUID TEST_SERVER_UUID = UUID.fromString("2F40EAF8-AAB4-14A3-F1BA-F61F35CDDBAA");
    public UUID TEST_NOTIFY_UUID = UUID.fromString("2F400002-AAB4-14A3-F1BA-F61F35CDDBAA");
    public UUID TEST_WRITE_UUID = UUID.fromString("2F400001-AAB4-14A3-F1BA-F61F35CDDBAA");



    /**获取当前时间**/
    public byte[] getCurrTime(){
        return new byte[]{0x00,0x02};
    }

    /**设置时间**/
    public byte[] syncTime(int year,int month,int day,int hour ,int minute,int second){
        byte[] timeByte = new byte[9];
        timeByte[0] = 0x07;
        timeByte[1] = 0x30;
        timeByte[2] = (byte) (year & 0x00ff);
        timeByte[3] = (byte) ((year>>8) & 0xff);
        timeByte[4] = (byte) (month & 0xff);
        timeByte[5] = (byte) (day & 0xff);
        timeByte[6] = (byte) (hour & 0xff);
        timeByte[7] = (byte) (minute & 0xff);
        timeByte[8] = (byte) (second & 0xff);
        return timeByte;
    }

    /**关机，复位**/
    public byte[] powerOff(int off){
        return new byte[]{0x05,0x60, (byte) off, (byte) 0xa1, (byte) 0xfe,0x74,0x69};
    }

    /**查找手表**/
    public byte[] findDevice(){
        return new byte[]{0x00,0x019};
    }

    //进入拍照
    public byte[] setIntoPhoto(){
        return new byte[]{0x00,0x0d};
    }

    //获取版本信息
    public byte[] getDeviceVersion(){
        return new byte[]{0x00,0x01};
    }

    //获取电量
    public byte[] getDeviceBattery(){
        return new byte[]{0x00,0x3f};
    }


    //同步用户信息
    public byte[] syncUserInfo(int year,int month,int day,int weight,int height,int sex,int maxHeart,int minHeart){
        byte[] userByte = new byte[12];
        userByte[0] = 0x0a;
        userByte[1] = 0x33;
        userByte[2] = (byte) ((year>>8)&0xff);
        userByte[3] = (byte) (year & 0xff);
        userByte[4] = (byte) (month & 0xff);
        userByte[5] = (byte) (day & 0xff);
        userByte[6] = (byte) sex;
        userByte[7] = (byte) (((weight * 100) >> 8) & 0xff);
        userByte[8] = (byte) (weight & 0xff);
        userByte[9] = (byte) (height & 0xff);
        userByte[10] = (byte) (maxHeart & 0xff);
        userByte[11] = (byte) (minHeart & 0xff);
        return userByte;
    }

    //获取用户信息
    public byte[] getUserInfo(){
        return new byte[]{0x00,0x05};
    }

    //设置目标步数
    public byte[] stepGoal(int step){
        byte[] at = Utils.intToByteArray(step);
        Log.e("SSS","--转换="+Utils.formatBtArrayToString(at));
        return new byte[]{0x03,0x38,at[0],at[1],at[2]};
    }
    //读取计步目标
    public byte[] readStepGoal(){
        return new byte[]{0x00,0x0C,0x00};
    }




    //获取久坐提醒
    public byte[] getLongSitData(){
        return new byte[]{0x00,0x03};
    }


    //设置久坐提醒
    public byte[] setLongSitData(int startHour,int startMinute,int interval ,int endHour,int endMinute){
        byte[] longSitByte = new byte[7];
        longSitByte[0] = 0x05;
        longSitByte[1] = 0x31;
        longSitByte[2] = (byte) (interval & 0xff);
        longSitByte[3] = (byte) (startHour & 0xff);
        longSitByte[4] = (byte) (startMinute & 0xff);
        longSitByte[5] = (byte) (endHour & 0xff);
        longSitByte[6] = (byte) (endMinute & 0xff);
        return longSitByte;

    }

    //获取抬腕亮屏
    public byte[] getWristData(){
        return new byte[]{0x00,0x08};
    }


    //设置抬腕亮屏
    public byte[] setWristData(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        byte[] wristByte = new byte[7];
        wristByte[0] =0x05;
        wristByte[1] = 0x37;
        wristByte[2] = (byte) (isOpen ? 1 : 0);
        wristByte[3] = (byte) (startHour & 0xff);
        wristByte[4] = (byte) (startMinute & 0xff);
        wristByte[5] = (byte) (endHour & 0xff);
        wristByte[6] = (byte) (endMinute & 0xff);
        return wristByte;
    }

    //获取心率开关
    public byte[] getHeartStatus(){
        return new byte[]{0x00,0x09};
    }

    //设置心率开关 1 : 开 ；0 ：关闭
    public byte[] setHeartStatus(boolean isOpen){
        return new byte[]{0x01,0x1a, (byte) (isOpen ? 1 : 0)};
    }

    //读取勿扰模式
    public byte[] getDNTStatus(){
        return new byte[]{0x00,0x06};
    }

    //设置勿扰模式
    public byte[] setDNTStatus(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute){
        byte[] dntByte = new byte[7];
        dntByte[0] = 0x05;
        dntByte[1] = 0x34;
        dntByte[2] = (byte) (isOpen ? 1 : 0);
        dntByte[3] = (byte) (startHour & 0xff);
        dntByte[4] = (byte) (startMinute & 0xff);
        dntByte[5] = (byte) (endHour & 0xff);
        dntByte[6] = (byte) (endMinute & 0xff);
        return dntByte;
    }


    //进入拍照模式
    public byte[] intoPhoto(){
        return new byte[]{0x00,0x0d};
    }


    //测量血氧 1开始，2结束
    public byte[] measureSpo2(boolean isOpen){
        return new byte[]{0x01,0x3d, (byte) (isOpen ? 1 : 2)};
    }

    //测量血压 1 开始，2停止
    public byte[] measureBloodStatus(boolean isOpen){
        return new byte[]{0x01,0x3b, (byte) (isOpen ? 1 : 2)};
    }

    //测量心率
    public byte[] measureHeartStatus(boolean isOpen){
        return new byte[]{0x01,0x50 , (byte) (isOpen ? 1 : 2)};
    }

    //设置通用
    public byte[] setCommonData(boolean isKm,boolean is24Hour,boolean is24HourHeart,boolean isTmp){
        byte[] connByte = new byte[12];
        connByte[0] = 0x0a;
        connByte[1] = 0x32 & 0xff;


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(0);
        stringBuilder.append(0);
        stringBuilder.append(isTmp?1:0);
        stringBuilder.append(is24HourHeart?0:1);
        stringBuilder.append(0);
        stringBuilder.append(is24Hour?1:0);
        stringBuilder.append(0);
        stringBuilder.append(isKm?0:1);
        Log.e("TAG","---sb="+stringBuilder.toString());

        byte b1 = Utils.bitToByte(stringBuilder.toString());
        connByte[3] =b1;

        StringBuilder stringBuilder2 = new StringBuilder();
        for(int i = 0;i<6;i++){
            stringBuilder2.append(0);
        }
        stringBuilder2.append(1);
        stringBuilder2.append(1);
        byte b2 = Utils.bitToByte(stringBuilder2.toString());
        connByte[4] = b2;

        return connByte;


    }


    //获取通用设置
    public byte[] getCommonSetData(){
        return new byte[]{0x00,0x04};
    }

    //获取内置表盘
    public byte[] getLocalDial(){
        return new byte[]{0x00,0x0a};
    }

    //设置内置表盘 1开始
    public byte[] setLocalDial(int index){
        return new byte[]{0x01,0x1b, (byte) index};
    }

    //获取背光等级
    public byte[] getBackLight(){
        return new byte[]{0x00,0x0b};
    }

    /**
     * 设置背光等级
     * @param light 亮度范围等级
     * @param timeInterval 亮度时长
     * @return
     */
    public byte[] setBackLight(int light,int timeInterval){
        return new byte[]{0x02,0x36, (byte) light, (byte) timeInterval};
    }

    //读取当天数据 0为当前，1是昨天
    public byte[] getDaySport(int day){
        return new byte[]{0x01,0x40, (byte) day};
    }

    //获取汇总数据，返回距离，卡路里等信息
    public byte[] getCountData(int day){
        return new byte[]{0x01,0x45, (byte) day};
    }

    /**读取闹钟从0开始第一个，1第二个，2第三个，总共3个**/
    public byte[] readAlarm(int alarmId){
        return new byte[]{0x00,0x07, (byte) alarmId};
    }

    /**设置闹钟**/
    public byte[] setAlarm(int index,boolean isOpen,int repeat,int hour,int minute){
        byte[] alarmByte = new byte[20];
        alarmByte[0] = 0x12;
        alarmByte[1] = 0x35;
        alarmByte[2] = (byte) index;
        alarmByte[3] = (byte) (isOpen ? 1 : 0);
        alarmByte[4] = (byte) repeat;
        alarmByte[5] = (byte) (hour & 0xff);
        alarmByte[6] = (byte) (minute &0xff);
        return alarmByte;
    }


    /**
     * 发送天气
     * @param index 下标
     * @param airQuality 空气质量
     * @param temperature 当前温度
     * @param heightT 最高温度
     * @param lowT 最低温度
     * @param weatherStatus 天气
     * @return
     */
    public byte[] weatherList(int index,int airQuality,int temperature,int heightT,int lowT,int weatherStatus){


        byte[] weatherByte = new byte[]{0x04,0x12, (byte) index, (byte) airQuality, (byte) (airQuality>>8), (byte) (temperature &0xff), (byte) (heightT & 0xff), (byte) (lowT &0xff), (byte) (weatherStatus & 0xff)};

        return weatherByte;
    }


    /**天气数据**/
    public ArrayList<byte[]> weatherListByte(String cityName){
        ArrayList<byte[]> list = new ArrayList<>();

        byte[] cityByte = cityName.getBytes();
        byte[] bt1 = new byte[20];
        bt1[0] = 0x02;
        bt1[1] = 12;
        bt1[2] = 0x01;
        byte[] tmpCity = new byte[17];
        if(cityByte.length>=17){
            System.arraycopy(cityByte,0,tmpCity,0,17);
        }else{
            System.arraycopy(cityByte,0,tmpCity,0,cityByte.length);
        }

        System.arraycopy(tmpCity,0,bt1,2,tmpCity.length);
        list.add(bt1);

        for(int i = 2;i<9;i++){
            byte[] secondByte = new byte[20];
            secondByte[0] = 0x04;
            secondByte[1] = 12;
            secondByte[2] = (byte) i;
            secondByte[3] = 0x00;
            secondByte[4] = 0x20;
            secondByte[5] = (byte) (20 >> 8);
            secondByte[6] = 0x21;
            secondByte[7] = 0x30;
            secondByte[8] = 0x10;
            secondByte[9] = 0x25;
            list.add(secondByte);
        }


        return list;
    }


    //发送表盘，
    //从1开始，到1,2,3,4,0
    public byte[] sendCurrWatchFace(int index){
        return new byte[]{0x01,0x61, (byte) index, (byte) 0xA1, (byte) 0xFE,0x74,0x69,0x02};
    }




    /**发送音乐**/
    public ArrayList<byte[]> sendMusic(String musicName,String musicCountTime,String musicPlayTime){

        ArrayList<byte[]> musicListByte = new ArrayList<>();

        //音乐名称
        byte[] nameByte = musicName.getBytes(StandardCharsets.UTF_8);

        byte[] musicByte = new byte[50];
        musicByte[0] = 0x01;
        musicByte[1] = 0x1f;
        musicByte[2] = 0x01;

        System.arraycopy(nameByte,0,musicByte,3,nameByte.length-1);


        musicListByte.add(musicByte);


        //音乐总时间
        byte[] countTimeByte = musicCountTime.getBytes(StandardCharsets.UTF_8);
        byte[] musicCountByte = new byte[50];
        musicCountByte[0] = 0x01;
        musicCountByte[1] = 0x1f;
        musicCountByte[2] = 0x02;
        System.arraycopy(countTimeByte,0,musicCountByte,3,countTimeByte.length-1);

        musicListByte.add(musicCountByte);

        //音乐播放时间
        byte[] playByteTime = musicPlayTime.getBytes(StandardCharsets.UTF_8);

        byte[] playByte = new byte[50];
        playByte[0] = 0x01;
        playByte[1] = 0x1f;
        playByte[2] = 0x03;
        System.arraycopy(playByteTime,0,playByte,3,playByteTime.length-1);
        musicListByte.add(playByte);

        return musicListByte;
    }


    /**提取锻炼数据**/
    public byte[] getExerciseByte(int num){
        return new byte[]{0x01,0x42, (byte) num};
    }


    /**清除所有锻炼数据**/
    public byte[] clearExerciseByte(){
       return new byte[]{0x01, (byte) 0x43,0x01};
    }

    /**进入工厂测试模式指令**/
    public byte[] intoTestModel(){
        return new byte[]{0x05, (byte) 0xfe,0x54, (byte) 0xa1, (byte) 0xfe,0x74,0x69};
    }

}
