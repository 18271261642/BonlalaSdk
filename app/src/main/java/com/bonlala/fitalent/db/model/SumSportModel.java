package com.bonlala.fitalent.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 总的运动数据，总步数，距离，卡路里
 * Created by Admin
 * Date 2022/8/9
 */
public class SumSportModel extends LitePalSupport {


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

    //总的步数
    private int sumStep;
    //总的距离 单位米
    private int sumDistance;
    //总的卡路里
    private int sumKcal;



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

    public int getSumStep() {
        return sumStep;
    }

    public void setSumStep(int sumStep) {
        this.sumStep = sumStep;
    }

    public int getSumDistance() {
        return sumDistance;
    }

    public void setSumDistance(int sumDistance) {
        this.sumDistance = sumDistance;
    }

    public int getSumKcal() {
        return sumKcal;
    }

    public void setSumKcal(int sumKcal) {
        this.sumKcal = sumKcal;
    }
}
