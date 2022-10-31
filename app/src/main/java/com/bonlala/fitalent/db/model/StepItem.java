package com.bonlala.fitalent.db.model;

import android.graphics.Point;

/**
 * 详细计步对象，序列化后存储
 * Created by Admin
 * Date 2022/8/26
 */
public class StepItem {

    private int step;


    private int hour;

    private long time;

    /**用于周的x坐标轴数据**/
    private String weekXStr;

    /**坐标轴 点击的时候给设置**/
    private Point point;


    //是否被选中
    private boolean isChecked;

    public StepItem() {
    }

    public StepItem(int step, int hour) {
        this.step = step;
        this.hour = hour;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getWeekXStr() {
        return weekXStr;
    }

    public void setWeekXStr(String weekXStr) {
        this.weekXStr = weekXStr;
    }
}
