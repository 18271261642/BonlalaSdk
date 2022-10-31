package com.bonlala.fitalent.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 单次心率，单次测量心率
 * Created by Admin
 * Date 2022/8/9
 */
public class SingleHeartModel extends LitePalSupport {

    //userId
    private String userId;
    //deviceId
    private String deviceId;
    //deviceMac
    private String deviceMac;
    private String deviceName;

    //日期 yyyy-MM-dd
    private String dayStr;
    //yyyy-MM-dd HH:mm:ss
    private String saveTimeStr;
    //long
    private long saveLongTime;

    //心率值
    private int heartValue;

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

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public String getSaveTimeStr() {
        return saveTimeStr;
    }

    public void setSaveTimeStr(String saveTimeStr) {
        this.saveTimeStr = saveTimeStr;
    }

    public long getSaveLongTime() {
        return saveLongTime;
    }

    public void setSaveLongTime(long saveLongTime) {
        this.saveLongTime = saveLongTime;
    }

    public int getHeartValue() {
        return heartValue;
    }

    public void setHeartValue(int heartValue) {
        this.heartValue = heartValue;
    }
}
