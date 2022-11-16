package com.bonlala.fitalent.db.model;

import android.content.Context;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.emu.DeviceNotifyType;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2022/11/15
 * @author Admin
 */
public class CommDbTimeModel extends LitePalSupport {

    /**
     * userId
     */
    private String userId;

    /**
     * mac
     */
    private String deviceMac;

    /**
     * 类型 0久坐、1勿扰、2抬腕
     */
    private String dbType;



    /**开关状态 0关，1开**/
    private int switchStatus;

    /**开始小时**/
    private int startHour;

    /**开始分钟**/
    private int startMinute;

    /**结束小时**/
    private int endHour;

    /**结束分钟**/
    private int endMinute;


    /**间隔 分钟**/
    private int level;


    /**
     * 获取开始和结束的时间
     */
    public String getStartAndEndTime(Context context){
        //久坐没有开关
        if(!dbType.equals(DeviceNotifyType.DB_LONG_DOWN_SIT_TYPE)){
            //未开启
            if(switchStatus == 0){

                return context.getResources().getString(R.string.string_not_opened);
            }
        }

        int start = startHour*60+startMinute;
        int end = endHour * 60 + endMinute;
        //开始和结束时间都为0，也是未开启
        if(start == 0 && end == 0){
            return context.getResources().getString(R.string.string_not_opened);
        }
        String startMinStr = String.format("%02d",startMinute);
        String endMinStr = String.format("%02d",endMinute);
        return end<=start ? ((startHour+":"+String.format("%02d",startMinute))+"~"+context.getResources().getString(R.string.string_next_day)+(endHour+":"+endMinStr)) : ((startHour+":"+startMinStr)+"~"+endHour+":"+endMinStr);
    }


    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
