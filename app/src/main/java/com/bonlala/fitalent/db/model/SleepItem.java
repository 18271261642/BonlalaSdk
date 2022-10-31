package com.bonlala.fitalent.db.model;

import android.graphics.Point;

/**
 * 睡眠数据源，序列化后存储
 * Created by Admin
 * Date 2022/8/10
 * @author Admin
 */
public class SleepItem {

    //开始时间
    private int startTime;
    //时长
    private int sleepLength;
    //结束时间
    private int endTime;
    //类型
    private int sleepType;

    private Point point;

    //是否被点击了，用于绘制图表时点击效果
    private boolean isClick;



    public SleepItem() {
    }

    public SleepItem(int startTime, int sleepLength, int endTime, int sleepType) {
        this.startTime = startTime;
        this.sleepLength = sleepLength;
        this.endTime = endTime;
        this.sleepType = sleepType;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getSleepLength() {
        return sleepLength;
    }

    public void setSleepLength(int sleepLength) {
        this.sleepLength = sleepLength;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getSleepType() {
        return sleepType;
    }

    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
    }


    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "SleepItem{" +
                "startTime=" + startTime +
                ", sleepLength=" + sleepLength +
                ", endTime=" + endTime +
                ", sleepType=" + sleepType +
                ", isClick=" + isClick +
                '}';
    }
}
