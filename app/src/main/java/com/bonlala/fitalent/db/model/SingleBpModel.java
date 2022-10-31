package com.bonlala.fitalent.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 单次测量血压
 * Created by Admin
 * Date 2022/8/10
 */
public class SingleBpModel extends LitePalSupport {

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

    //收缩压
    private int sysBp;
    //舒张压
    private int diastolicBp;


    public SingleBpModel() {
    }


    public SingleBpModel(long saveLongTime, int sysBp, int diastolicBp) {
        this.saveLongTime = saveLongTime;
        this.sysBp = sysBp;
        this.diastolicBp = diastolicBp;
    }

    public int getSysBp() {
        return sysBp;
    }

    public void setSysBp(int sysBp) {
        this.sysBp = sysBp;
    }

    public int getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(int diastolicBp) {
        this.diastolicBp = diastolicBp;
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


}
