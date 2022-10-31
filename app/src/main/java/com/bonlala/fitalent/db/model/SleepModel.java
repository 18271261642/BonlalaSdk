package com.bonlala.fitalent.db.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Created by Admin
 * Date 2022/8/10
 * @author Admin
 */
public class SleepModel extends LitePalSupport {


    //userId
    private String userId;
    //deviceId,名称+Mac
    private String deviceId;
    //mac
    private String deviceMac;
    //设备的name
    private String deviceName;
    //保存的日期yyyy-MM-dd
    private String saveDay;
    //保存一个时间搓
    private long saveLongTime;

    //入睡时间 分钟，eg:22:00 = 1320
    private int fallAsleepTime;
    //醒来时间
    private int wakeUpTime;


    //总的睡眠时长 分钟
    private int countSleepTime;
    //深睡时长 分钟
    private int deepTime;
    //浅睡时长
    private int lightTime;
    //清醒时间长
    private int awakeTime;

    //快速眼动时长
    private int remTime;
    //失眠时长
    private int insomnia;

    /**上午的睡眠
     * 一天1440分钟，睡觉是晚上22:00到第二天的8:00，所以需要两天合并
     * List<Integer>集合序列化
     */
    private String morningSleepStr;

    /**
     * 晚上的睡眠
     */
    private String nightSleepStr;


    //睡眠数据源，对象序列化转字符串存储
    /**
     *
     * com.bonlala.bonlalasdk.db.model.SleepItem 集合序列化字符串
     */
    private String sleepSource;



    public List<SleepItem> getSleepSourceList(){
        if(sleepSource == null)
            return null;
        List<SleepItem> list = new Gson().fromJson(sleepSource,new TypeToken<List<SleepItem>>(){}.getType());
        return list;
    }


    public String getMorningSleepStr() {
        return morningSleepStr;
    }

    public void setMorningSleepStr(String morningSleepStr) {
        this.morningSleepStr = morningSleepStr;
    }

    public String getNightSleepStr() {
        return nightSleepStr;
    }

    public void setNightSleepStr(String nightSleepStr) {
        this.nightSleepStr = nightSleepStr;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSaveDay() {
        return saveDay;
    }

    public void setSaveDay(String saveDay) {
        this.saveDay = saveDay;
    }

    public long getSaveLongTime() {
        return saveLongTime;
    }

    public void setSaveLongTime(long saveLongTime) {
        this.saveLongTime = saveLongTime;
    }

    public int getFallAsleepTime() {
        return fallAsleepTime;
    }

    public void setFallAsleepTime(int fallAsleepTime) {
        this.fallAsleepTime = fallAsleepTime;
    }

    public int getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(int wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public int getCountSleepTime() {
        return countSleepTime;
    }

    public void setCountSleepTime(int countSleepTime) {
        this.countSleepTime = countSleepTime;
    }

    public int getDeepTime() {
        return deepTime;
    }

    public void setDeepTime(int deepTime) {
        this.deepTime = deepTime;
    }

    public int getLightTime() {
        return lightTime;
    }

    public void setLightTime(int lightTime) {
        this.lightTime = lightTime;
    }

    public int getAwakeTime() {
        return awakeTime;
    }

    public void setAwakeTime(int awakeTime) {
        this.awakeTime = awakeTime;
    }

    public int getRemTime() {
        return remTime;
    }

    public void setRemTime(int remTime) {
        this.remTime = remTime;
    }

    public int getInsomnia() {
        return insomnia;
    }

    public void setInsomnia(int insomnia) {
        this.insomnia = insomnia;
    }

    public String getSleepSource() {
        return sleepSource;
    }

    public void setSleepSource(String sleepSource) {
        this.sleepSource = sleepSource;
    }
}
