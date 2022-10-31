package com.bonlala.fitalent.bean;

/**
 * Created by Admin
 * Date 2022/10/13
 * @author Admin
 */
public class ChartHrBean {

    /**
     * 分钟，1分钟一个 eg;10就是10分钟
     */
    private int time;

    /**
     * 心率值
     */
    private int hrValue;

    public ChartHrBean() {
    }

    public ChartHrBean(int time, int hrValue) {
        this.time = time;
        this.hrValue = hrValue;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getHrValue() {
        return hrValue;
    }

    public void setHrValue(int hrValue) {
        this.hrValue = hrValue;
    }
}
