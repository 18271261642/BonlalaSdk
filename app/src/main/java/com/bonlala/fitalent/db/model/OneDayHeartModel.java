package com.bonlala.fitalent.db.model;

import android.os.Build;

import org.litepal.crud.LitePalSupport;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一整天的连续心率
 * Created by Admin
 * Date 2022/8/10
 */
public class OneDayHeartModel extends LitePalSupport {

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



    //心率的集合，每分钟一个点，不够补0，24小时总共1440个
    private List<Integer> heartList;

    //最大值
    public int getMaxHeart(){
        if(heartList == null || heartList.isEmpty())
            return 0;
        return Collections.max(heartList);
    }


    //最小值
    public int getMinHeart(){
        if(heartList == null || heartList.isEmpty())
            return 0;
        return Collections.min(heartList);
    }


    //平均值
    public int getAvgHeart(){
        if(heartList == null || heartList.isEmpty())
            return 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            heartList.stream().collect(Collectors.averagingInt(Integer::intValue));
        }else{
            int count = 0;
            int num = 0;
            for(Integer integer : heartList){
                if(integer != 0){
                    count+=integer;
                    num++;
                }
            }

            return count/num;
        }
        return 0;
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

    public List<Integer> getHeartList() {
        return heartList;
    }

    public void setHeartList(List<Integer> heartList) {
        this.heartList = heartList;
    }
}
