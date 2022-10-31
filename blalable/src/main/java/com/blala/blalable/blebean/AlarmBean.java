package com.blala.blalable.blebean;

/**
 * Created by Admin
 * Date 2022/9/14
 */
public class AlarmBean {

    //序号，0开始
    private  int alarmIndex;

    //开关 true开；false关
    private boolean isOpen;

    //周期
    private byte repeat;

    //小时
    private int hour;

    //分钟
    private int minute;

    //信息
    private String msg;

    public int getAlarmIndex() {
        return alarmIndex;
    }

    public void setAlarmIndex(int alarmIndex) {
        this.alarmIndex = alarmIndex;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public byte getRepeat() {
        return repeat;
    }

    public void setRepeat(byte repeat) {
        this.repeat = repeat;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "AlarmBean{" +
                "alarmIndex=" + alarmIndex +
                ", isOpen=" + isOpen +
                ", repeat=" + repeat +
                ", hour=" + hour +
                ", minute=" + minute +
                ", msg='" + msg + '\'' +
                '}';
    }
}
