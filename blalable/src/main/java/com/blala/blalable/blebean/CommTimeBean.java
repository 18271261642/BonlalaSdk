package com.blala.blalable.blebean;

/**
 * Created by Admin
 * Date 2022/9/5
 * @author Admin
 */
public class CommTimeBean {

    //开关状态 0关，1开
    private int switchStatus;

    //开始小时
    private int startHour;

    //开始分钟
    private int startMinute;

    //结束小时
    private int endHour;

    //结束分钟
    private int endMinute;


    //间隔 分钟
    private int level;



    public CommTimeBean() {
    }

    public CommTimeBean(int switchStatus, int startHour, int startMinute, int endHour, int endMinute) {
        this.switchStatus = switchStatus;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
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

    @Override
    public String toString() {
        return "CommTimeBean{" +
                "switchStatus=" + switchStatus +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", level=" + level +
                '}';
    }
}
