package com.bonlala.fitalent.bean;

/**
 * 用于闹钟重复周期周的选择
 * Created by Admin
 * Date 2022/9/15
 */
public class WeekBean {

    //周日=1，周一=2，周二=4，周三8，周四=16，周五=32，周六=64
    private int repeatValue;

    //周的文字
    private String weekStr;


    public WeekBean(int repeatValue, String weekStr) {
        this.repeatValue = repeatValue;
        this.weekStr = weekStr;
    }

    public int getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(int repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getWeekStr() {
        return weekStr;
    }

    public void setWeekStr(String weekStr) {
        this.weekStr = weekStr;
    }

    @Override
    public String toString() {
        return "WeekBean{" +
                "repeatValue=" + repeatValue +
                ", weekStr='" + weekStr + '\'' +
                '}';
    }
}
