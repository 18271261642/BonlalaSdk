package com.bonlala.fitalent.bean;


/**
 * 绘制心率带分组的进度条，按每秒钟一个单位绘制
 * Created by Admin
 * Date 2022/12/9
 * @author Admin
 */
public class HrBeltGroupBean {

    /**
     * 类型 0正常进度，1中途休息或者间隔
     */
    private int type;

    /**
     * 开始时间 秒
     */
    private int startTime;

    /**
     * 结束时间
     */
    private int endTime;


    public HrBeltGroupBean() {
    }

    public HrBeltGroupBean(int type, int startTime, int endTime) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
