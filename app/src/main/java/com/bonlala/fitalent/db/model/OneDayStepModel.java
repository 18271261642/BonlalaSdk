package com.bonlala.fitalent.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 一整天的连续计步
 * Created by Admin
 * Date 2022/8/10
 * @author Admin
 */
public class OneDayStepModel extends LitePalSupport {

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

    //当天总的步数
    private int dayStep;
    //当天总的卡路里 卡
    private int dayCalories;
    //当天总的距离 米
    private int dayDistance;




    //详细计步，每一个小时一个点
    private String detailStep;



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

    public String getDetailStep() {
        return detailStep;
    }

    public void setDetailStep(String detailStep) {
        this.detailStep = detailStep;
    }

    public int getDayStep() {
        return dayStep;
    }

    public void setDayStep(int dayStep) {
        this.dayStep = dayStep;
    }

    public int getDayCalories() {
        return dayCalories;
    }

    public void setDayCalories(int dayCalories) {
        this.dayCalories = dayCalories;
    }

    public int getDayDistance() {
        return dayDistance;
    }

    public void setDayDistance(int dayDistance) {
        this.dayDistance = dayDistance;
    }
}
